/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.demo;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class Live2dRenderer implements GLSurfaceView.Renderer {

    private AccelHelper mAccelHelper;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtils.d("onSurfaceCreated");
        JniBridgeJava.nativeOnSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.d("onSurfaceChanged W=" + width + ", height="+height);
        JniBridgeJava.nativeOnSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        JniBridgeJava.nativeOnDrawFrame();
        if (mAccelHelper != null){
            mAccelHelper.update();
            if (mAccelHelper.getShake() > 2.0f){
                LogUtils.d("TODO: handle shake");
                mAccelHelper.resetShake();
            }
        }
    }

    public void setAccelHelper(AccelHelper accelHelper) {
        this.mAccelHelper = accelHelper;
    }
}
