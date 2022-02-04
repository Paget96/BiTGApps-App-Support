package com.paget96.bitgapps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.paget96.bitgapps.Links.BITGAPPS_APP_RELEASE_CHANGELOG
import com.paget96.bitgapps.Links.BITGAPPS_APP_RELEASE_PROP
import com.paget96.bitgapps.Links.BITGAPPS_DOWNLOAD
import com.paget96.bitgapps.Links.BITGAPPS_GITHUB
import com.paget96.bitgapps.Links.BITGAPPS_TELEGRAM
import com.paget96.bitgapps.Links.BITGAPPS_XDA
import com.paget96.bitgapps.Links.PAGET96_DEV_PROFILE
import com.paget96.bitgapps.Links.THE_HITMAN_GITHUB
import com.paget96.bitgapps.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    // Variables
    private var binding: ActivityMainBinding? = null
    private val bitGappsPropFile = "/system/etc/g.prop"
    private val utils = Utils()
    private val isBitGappsInstalled = utils.fileExists(bitGappsPropFile, false)

    private fun viewState() {
        checkForUpdate()

        binding?.apply {
            if (isBitGappsInstalled) {
                gappsInfo.apply {
                    root.visibility = View.VISIBLE

                    gappsInfo.gappsPackage.text = utils.splitString(getLineContent(1), "=", 1)
                    gappsVersion.text = utils.splitString(getLineContent(4), "=", 1)
                    gappsPlatform.text = utils.splitString(getLineContent(2), "=", 1)
                    gappsSdk.text = utils.splitString(getLineContent(3), "=", 1)
                    buildDate.text = utils.splitString(getLineContent(5), "=", 1)
                    gappsBuildId.text = utils.splitString(getLineContent(6), "=", 1)
                    developer.text = utils.splitString(getLineContent(7), "=", 1)
                }

                gappsNotInstalled.apply {
                    root.visibility = View.GONE
                }
            } else {
                gappsInfo.apply {
                    root.visibility = View.GONE
                }

                gappsNotInstalled.apply {
                    root.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onClick() {
        binding?.apply {
            about.setOnClickListener {
                val dialog = MaterialAlertDialogBuilder(this@MainActivity)

                val customAlertDialogView = LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.overflow_about_app, null, false)

                dialog.apply {
                    setCancelable(true)
                    setView(customAlertDialogView)
                }

                val appVersion = customAlertDialogView.findViewById<TextView>(R.id.app_version)
                appVersion.text = String.format("v%s", BuildConfig.VERSION_NAME)

                val paget96 = customAlertDialogView.findViewById<LinearLayout>(R.id.paget96)
                paget96.setOnClickListener {
                    utils.openLink(
                        this@MainActivity,
                        PAGET96_DEV_PROFILE, true
                    )
                }

                val theHitman = customAlertDialogView.findViewById<LinearLayout>(R.id.the_hitman)
                theHitman.setOnClickListener {
                    utils.openLink(
                        this@MainActivity,
                        THE_HITMAN_GITHUB, true
                    )
                }

                dialog.show()
            }

            gappsNotInstalled.download.setOnClickListener {
                utils.openLink(
                    this@MainActivity,
                    BITGAPPS_DOWNLOAD, true
                )
            }

            xda.setOnClickListener {
                utils.openLink(
                    this@MainActivity,
                    BITGAPPS_XDA, true
                )
            }

            telegram.setOnClickListener {
                utils.openLink(
                    this@MainActivity,
                    BITGAPPS_TELEGRAM, true
                )
            }

            github.setOnClickListener {
                utils.openLink(
                    this@MainActivity,
                    BITGAPPS_GITHUB, true
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dynamic color changing
        DynamicColors.applyIfAvailable(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        binding = ActivityMainBinding.inflate(layoutInflater)

        onClick()
        viewState()

        setContentView(binding?.root)
    }

    private fun getLineContent(line: Int): String {
        return utils.runCommand("cat " + bitGappsPropFile + " | sed -n " + line + "p", false)
    }

    private fun checkForUpdate() {
        if (isBitGappsInstalled)
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    var buildId = ""
                    val buildIdFileUrl = URL(BITGAPPS_APP_RELEASE_PROP)
                    val buildIdBufferReader =
                        BufferedReader(InputStreamReader(buildIdFileUrl.openStream()))
                    var buildIdStringBuffer: String?
                    while (buildIdBufferReader.readLine()
                            .also { buildIdStringBuffer = it } != null
                    ) {
                        buildId += buildIdStringBuffer
                    }
                    buildIdBufferReader.close()

                    var changelog: String? = ""
                    val changelogFileUrl = URL(BITGAPPS_APP_RELEASE_CHANGELOG)
                    val changelogBufferReader =
                        BufferedReader(InputStreamReader(changelogFileUrl.openStream()))
                    var changelogStringBuffer: String?
                    while (changelogBufferReader.readLine()
                            .also { changelogStringBuffer = it } != null
                    ) {
                        changelog += changelogStringBuffer + "\n"
                    }
                    changelogBufferReader.close()

                    withContext(Dispatchers.Main) {
                        if (utils.splitString(
                                buildId,
                                "=",
                                1
                            ) != utils.splitString(getLineContent(6), "=", 1)
                        ) {
                            Toast.makeText(
                                this@MainActivity,
                                "Update Available!",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            binding?.apply {
                                layoutNoGappsUpdate.root.visibility = View.GONE
                                layoutGappsUpdate.root.visibility = View.VISIBLE
                                layoutGappsUpdate.bitgappsRelease.text = buildId
                                layoutGappsUpdate.changelog.text = changelog

                                layoutGappsUpdate.downloadUpdate.setOnClickListener {
                                    utils.openLink(
                                        this@MainActivity,
                                        BITGAPPS_DOWNLOAD, true
                                    )
                                }
                            }
                        } else {
                            binding?.apply {
                                layoutGappsUpdate.root.visibility = View.GONE
                                layoutNoGappsUpdate.root.visibility = View.VISIBLE

                                layoutNoGappsUpdate.checkForUpdate.setOnClickListener {
                                    checkForUpdate()
                                }
                            }
                        }

                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        checkForUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        utils.closeShell()
    }
}