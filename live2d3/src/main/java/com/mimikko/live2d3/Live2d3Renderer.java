/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.mimikko.live2d3;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;

public class Live2d3Renderer implements GLSurfaceView.Renderer {

    private AccelHelper mAccelHelper;
    private Live2d3Manager mManger;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        JniBridgeJava.nativeOnSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        JniBridgeJava.nativeOnSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        JniBridgeJava.nativeOnDrawFrame();
        if (mAccelHelper != null){
            mAccelHelper.update();
            if (mAccelHelper.getShake() > 2.0f){
                mAccelHelper.resetShake();
            }
        }
        if (mManger != null){
            mManger.update();
        }
    }

    public void setAccelHelper(AccelHelper accelHelper) {
        this.mAccelHelper = accelHelper;
    }

    public void setManger(Live2d3Manager manger) {
        this.mManger = manger;
    }
}
