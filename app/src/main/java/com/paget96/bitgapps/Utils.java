package com.paget96.bitgapps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AndroidRuntimeException;
import android.view.View;

import com.topjohnwu.superuser.Shell;

import java.io.IOException;
import java.util.List;

public class Utils {

    public String runCommand(String command, boolean root) {
        List<String> output;

        output = root ? Shell.su(command).exec().getOut() : Shell.sh(command).exec().getOut();

        StringBuilder sb = new StringBuilder();
        for (String s : output) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    public void closeShell() {
        try {
            Shell shell = Shell.getCachedShell();
            if (shell != null)
                shell.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean fileExists(String file, boolean root) {
        String result =
                runCommand("if [ -e " + file + " ]; then echo true; fi", root);
        return (result != null && result.contains("true"));
    }

    public String splitString(String string, String regex, int element) {
        try {
            return string.split(regex)[element];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "--";
        }
    }

    // Parses and open links.
    public void openLink(Context context, String link) {
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public void expandView(View expandedLayout, View animateArrow) {
        try {
            animateArrow.animate().rotation(180).setDuration(500).start();
        } catch (AndroidRuntimeException are) {
            are.printStackTrace();
        }
        expandedLayout.setVisibility(View.VISIBLE);
    }

    public void collapseView(View expandedLayout, View animateArrow) {
        try {
            animateArrow.animate().rotation(0).setDuration(500).start();
        } catch (AndroidRuntimeException are) {
            are.printStackTrace();
        }
        expandedLayout.setVisibility(View.GONE);
    }

    public void expandCollapseView(View expandedLayout, View animateArrow) {
        if (expandedLayout.isShown()) {
            collapseView(expandedLayout, animateArrow);
        } else {
            expandView(expandedLayout, animateArrow);
        }
    }
}
