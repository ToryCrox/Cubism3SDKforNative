/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.mimikko.live2d3;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.mimikko.live2d3.image.GLImageHandler;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class Live2d3Renderer implements GLSurfaceView.Renderer {

    private AccelHelper mAccelHelper;
    private GLImageHandler mImageHandler;

    public void setImageHandler(GLImageHandler imageHandler){
        mImageHandler = imageHandler;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (mImageHandler != null){
            mImageHandler.onSurfaceCreated(gl, config);
        }
        JniBridgeJava.nativeOnSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        JniBridgeJava.nativeOnSurfaceChanged(width, height);
        if (mImageHandler != null){
            mImageHandler.onSurfaceChanged(gl, width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (mImageHandler != null){
            mImageHandler.onDrawFrame(gl);
        }
        JniBridgeJava.nativeOnDrawFrame();
        if (mAccelHelper != null){
            mAccelHelper.update();
            if (mAccelHelper.getShake() > 2.0f){
                mAccelHelper.resetShake();
            }
        }
    }

    public void setAccelHelper(AccelHelper accelHelper) {
        this.mAccelHelper = accelHelper;
    }

}
