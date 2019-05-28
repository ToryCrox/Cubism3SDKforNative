/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.demo;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.live2d.demo.utils.SystemBarUtils;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Live2dDemoManager mLive2dManager;
    int mIndex=0;
    List<String> mDirs = Arrays.asList(
            "RURI_NEW/琉璃6_新增表情.model3.json",
            "RURI/RURI.model3.json",
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
        mLive2dManager = new Live2dDemoManager(this);
        container.addView(mLive2dManager.createView(this));

        getLifecycle().addObserver(mLive2dManager);

        mLive2dManager.loadModel(mDirs.get(mIndex));
        //mLive2dManager.setBackgroundImage(ContextCompat.getDrawable(this, R.drawable.bg_pic_test2));
        findViewById(R.id.btn_change)
                .setOnClickListener(v -> showChangeDialog());
    }

    private void showChangeDialog() {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, Live2dWallpaperService.class));
        startActivity(intent);
/*        ViewGroup container = findViewById(R.id.container);
        new AlertDialog.Builder(this)
                .setTitle("切换角色")
                .setSingleChoiceItems(mDirs.toArray(new String[mDirs.size()]), mIndex, (dialog, which) -> {
                    dialog.dismiss();
                    mLive2dManager.loadModel(mDirs.get(which));
                    mIndex = which;
                }).show();*/
    }
}
