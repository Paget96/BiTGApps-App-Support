package com.paget96.bitgapps;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    // Variables
    private final Utils utils = new Utils();
    private TextView gappsInstallState, gappsPackage, platform, sdk, version, buildDate, buildId, developer;
    private MaterialCardView xda, telegram, gitHub;
    private ImageView expandArrow;
    private MaterialButton getBitGapps, exitApp;
    private LinearLayout buttonHolder, mainLayoutHolder, moreInfo;

    private void initializeViews() {
        mainLayoutHolder = findViewById(R.id.main_layout_holder);
        gappsInstallState = findViewById(R.id.gapps_install_state);
        buttonHolder = findViewById(R.id.button_holder);
        expandArrow = findViewById(R.id.expand_arrow);
        moreInfo = findViewById(R.id.more_info);

        gappsPackage = findViewById(R.id.gapps_package);
        platform = findViewById(R.id.platform);
        sdk = findViewById(R.id.sdk);
        version = findViewById(R.id.version);
        buildDate = findViewById(R.id.build_date);
        buildId = findViewById(R.id.build_id);
        developer = findViewById(R.id.developer);

        getBitGapps = findViewById(R.id.get_bitgapps);
        exitApp = findViewById(R.id.exit);
        xda = findViewById(R.id.xda);
        telegram = findViewById(R.id.telegram);
        gitHub = findViewById(R.id.github);
    }

    private void getText() {
        if (utils.fileExists("/system/etc/g.prop", true)) {
            gappsInstallState.setText("BitGapps installed");
            mainLayoutHolder.setClickable(true);
            buttonHolder.setVisibility(View.GONE);
            expandArrow.setVisibility(View.VISIBLE);

            gappsPackage.setText(utils.splitString(getLineContent(1), "=", 1));
            version.setText(utils.splitString(getLineContent(4), "=", 1));

            platform.setText(String.format("Platform: %s", utils.splitString(getLineContent(2), "=", 1)));
            sdk.setText(String.format("SDK: %s", utils.splitString(getLineContent(3), "=", 1)));
            buildDate.setText(String.format("Build date: %s", utils.splitString(getLineContent(5), "=", 1)));
            buildId.setText(String.format("Build ID: %s", utils.splitString(getLineContent(6), "=", 1)));
            developer.setText(String.format("Developer: %s", utils.splitString(getLineContent(7), "=", 1)));
        } else {
            gappsInstallState.setText("BitGapps not installed");
            mainLayoutHolder.setClickable(false);
            buttonHolder.setVisibility(View.VISIBLE);
            expandArrow.setVisibility(View.GONE);
            moreInfo.setVisibility(View.GONE);
        }
    }

    private void onClick() {
        mainLayoutHolder.setOnClickListener(v -> utils.expandCollapseView(moreInfo, expandArrow));
        getBitGapps.setOnClickListener(v -> utils.openLink(MainActivity.this, "https://bitgapps.github.io"));
        exitApp.setOnClickListener(v -> finish());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.closeShell();
    }
}
