package com.paget96.bitgapps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.topjohnwu.superuser.Shell;

import java.util.List;

public class Utils {

    public String runCommand(String command, boolean root) {
        List<String> output;

        if (root)
            output = Shell.su(command).exec().getOut();
        else
            output = Shell.sh(command).exec().getOut();

        StringBuilder sb = new StringBuilder();
        for (String s : output) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    // Parses and open links.
    public void openLink(Context context, String link) {
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}
