package com.paget96.bitgapps

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.util.AndroidRuntimeException
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import com.topjohnwu.superuser.Shell
import java.io.IOException

class Utils {
    companion object {
        init {
            /* Shell.Config methods shall be called before any shell is created
         * This is the why in this example we call it in a static block
         * The followings are some examples, check Javadoc for more details */
            Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.NON_ROOT_SHELL)
                    .setFlags(Shell.FLAG_NON_ROOT_SHELL)
                    .setTimeout(10)
            )
        }
    }

    /**
     * Returns a color from an attribute reference.
     *
     * @param context Pass the activity context, not the application context
     * @param attr    The attribute reference to be resolved
     *
     * @return int array of color value
     */
    @ColorInt
    fun getColorFromAttr(context: Context, @AttrRes attr: Int): Int {
        return with(TypedValue()) {
            context.theme.resolveAttribute(attr, this, true)
            this.data
        }
    }


    fun runCommand(command: String?, root: Boolean): String {
        val output: List<String> =
            if (root) Shell.su(command).exec().out else Shell.sh(command).exec().out
        val sb = StringBuilder()
        for (s in output) {
            sb.append(s)
            sb.append("\n")
        }
        return sb.toString().trim { it <= ' ' }
    }

    fun closeShell() {
        try {
            val shell = Shell.getCachedShell()
            shell?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun fileExists(file: String, root: Boolean): Boolean {
        val result = runCommand("if [ -e $file ]; then echo true; fi", root)
        return result.contains("true")
    }

    fun splitString(string: String, regex: String?, element: Int): String {
        return try {
            string.split(regex!!).toTypedArray()[element]
        } catch (e: ArrayIndexOutOfBoundsException) {
            "--"
        }
    }

    /**
     * Use this method to open a browser link
     *
     */
    private fun openBrowserLink(context: Context, link: String?) = try {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No Internet Browser installed", Toast.LENGTH_LONG).show()
    }

    // Parses and open links.
    fun openLink(context: Context, link: String?, useCustomTabs: Boolean) {
        if (useCustomTabs) {
            showCusomTabs(context, Uri.parse(link))
        } else {
            openBrowserLink(context, link)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun launchNativeApi30(context: Context, uri: Uri?): Boolean {
        val nativeAppIntent = Intent(Intent.ACTION_VIEW, uri)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
            )
        return try {
            context.startActivity(nativeAppIntent)
            true
        } catch (ex: ActivityNotFoundException) {
            false
        }
    }

    private fun launchNativeBeforeApi30(context: Context, uri: Uri): Boolean {
        val pm = context.packageManager

        // Get all Apps that resolve a generic url
        val browserActivityIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts("http", "", null))
        val genericResolvedList: Set<String> = extractPackageNames(
            pm.queryIntentActivities(browserActivityIntent, 0)
        )

        // Get all apps that resolve the specific Url
        val specializedActivityIntent = Intent(Intent.ACTION_VIEW, uri)
            .addCategory(Intent.CATEGORY_BROWSABLE)
        val resolvedSpecializedList = extractPackageNames(
            pm.queryIntentActivities(specializedActivityIntent, 0)
        )

        // Keep only the Urls that resolve the specific, but not the generic
        // urls.
        resolvedSpecializedList.removeAll(genericResolvedList)

        // If the list is empty, no native app handlers were found.
        if (resolvedSpecializedList.isEmpty()) {
            return false
        }

        // We found native handlers. Launch the Intent.
        specializedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(specializedActivityIntent)
        return true
    }

    fun getNativeAppPackage(context: Context, uri: Uri?): Set<String> {
        val pm = context.packageManager

        //Get all Apps that resolve a generic url
        val browserActivityIntent = Intent(Intent.ACTION_VIEW, uri)
        val genericResolvedList: Set<String> =
            extractPackageNames(pm.queryIntentActivities(browserActivityIntent, 0))

        //Get all apps that resolve the specific Url
        val specializedActivityIntent = Intent(Intent.ACTION_VIEW, uri)
        val resolvedSpecializedList =
            extractPackageNames(pm.queryIntentActivities(specializedActivityIntent, 0))

        //Keep only the Urls that resolve the specific, but not the generic urls
        resolvedSpecializedList.removeAll(genericResolvedList)
        return resolvedSpecializedList
    }

    private fun extractPackageNames(resolveInfos: List<ResolveInfo>): MutableSet<String> {
        val packageNameSet: MutableSet<String> = HashSet()
        for (ri in resolveInfos) {
            packageNameSet.add(ri.activityInfo.packageName)
        }
        return packageNameSet
    }

    private fun showCusomTabs(context: Context?, uri: Uri?) {
        val launched = if (Build.VERSION.SDK_INT >= 30)
            launchNativeApi30(context!!, uri)
        else
            launchNativeBeforeApi30(context!!, uri!!)

        if (!launched) {
            try {
                CustomTabsIntent.Builder()
                    .build()
                    .launchUrl(context, uri!!)
            } catch (e: ActivityNotFoundException) {
                openBrowserLink(context, uri.toString())
            }
        }
    }

    fun expandView(expandedLayout: View, animateArrow: View) {
        try {
            animateArrow.animate().rotation(180f).setDuration(500).start()
        } catch (are: AndroidRuntimeException) {
            are.printStackTrace()
        }
        expandedLayout.visibility = View.VISIBLE
    }

    fun collapseView(expandedLayout: View, animateArrow: View) {
        try {
            animateArrow.animate().rotation(0f).setDuration(500).start()
        } catch (are: AndroidRuntimeException) {
            are.printStackTrace()
        }
        expandedLayout.visibility = View.GONE
    }

    fun expandCollapseView(expandedLayout: View, animateArrow: View) {
        if (expandedLayout.isShown) {
            collapseView(expandedLayout, animateArrow)
        } else {
            expandView(expandedLayout, animateArrow)
        }
    }
}