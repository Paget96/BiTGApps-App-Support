package com.paget96.bitgapps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.topjohnwu.superuser.Shell;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Variables
    private Utils utils = new Utils();
    private TextView gappsPackage, platform, sdk, version, buildDate, buildId, developer;
    private MaterialCardView xda, telegram, gitHub;

    private void initializeViews() {
        gappsPackage = findViewById(R.id.gapps_package);
        platform = findViewById(R.id.platform);
        sdk = findViewById(R.id.sdk);
        version = findViewById(R.id.version);
        buildDate = findViewById(R.id.build_date);
        buildId = findViewById(R.id.build_id);
        developer = findViewById(R.id.developer);

        xda = findViewById(R.id.xda);
        telegram = findViewById(R.id.telegram);
        gitHub = findViewById(R.id.github);
    }

    private void getText() {
        gappsPackage.setText("Gapps package: "+getLineContent(1).split("=")[1]);
        platform.setText("Platform: "+getLineContent(2).split("=")[1]);
        sdk.setText("SDK: "+getLineContent(3).split("=")[1]);
        version.setText("Version: "+getLineContent(4).split("=")[1]);
        buildDate.setText("Build date: "+getLineContent(5).split("=")[1]);
        buildId.setText("Build ID: "+getLineContent(6).split("=")[1]);
        developer.setText("Developer: "+getLineContent(7).split("=")[1]);
    }

    private void onClick() {
        xda.setOnClickListener(v -> utils.openLink(MainActivity.this, "https://forum.xda-developers.com/android/software/custom-bitgapps-android-t4012165"));
        telegram.setOnClickListener(v -> utils.openLink(MainActivity.this, "https://t.me/bitgapps_official"));
        gitHub.setOnClickListener(v -> utils.openLink(MainActivity.this, "https://github.com/BiTGApps"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        getText();
        onClick();
    }

    public String getLineContent(int line) {
        return utils.runCommand("cat /system/etc/g.prop | sed -n " + line + "p", true);
    }
}
