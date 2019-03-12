/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.live2d.demo.utils.SystemBarUtils;

public class MainActivity extends AppCompatActivity {

    Live2dManager mLive2dManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBarUtils.translucentNavBar(this);
        SystemBarUtils.translucentStatusBar(this);

        mLive2dManager = new Live2dManager(this);
        setContentView(mLive2dManager.getLive2dView());


        getLifecycle().addObserver(mLive2dManager);
    }
}
