/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.demo;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.live2d.demo.utils.SystemBarUtils;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Live2dManager mLive2dManager;
    int mIndex=0;
    List<String> mDirs = Arrays.asList( "RURI/RURI.model3.json",
            "Hiyori/Hiyori.model3.json",
            "Haru/Haru.model3.json",
            "Mark/Mark.model3.json");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBarUtils.translucentNavBar(this);
        SystemBarUtils.translucentStatusBar(this);

        setContentView(R.layout.activity_main);
        ViewGroup container = findViewById(R.id.container);
        mLive2dManager = new Live2dManager(this);
        container.addView(mLive2dManager.getLive2dView());

        
        getLifecycle().addObserver(mLive2dManager);
        
        findViewById(R.id.btn_change)
                .setOnClickListener(v -> showChangeDialog());

        JniBridgeJava.nativeRoadModel(mDirs.get(mIndex));
    }

    private void showChangeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("切换角色")
                .setSingleChoiceItems(mDirs.toArray(new String[mDirs.size()]), mIndex, (dialog, which) -> {
                    dialog.dismiss();
                    JniBridgeJava.nativeRoadModel(mDirs.get(which));
                    mIndex = which;
                }).show();
    }
}
