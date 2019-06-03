/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.mimikko.live2d3;

public class JniBridgeJava {
    private static final String TAG = "JniBridgeJava";

    private static final String LIBRARY_NAME = "JniBridge";
    private static IPlatform3Manager sIPlatform3Manager;

    static {
        System.loadLibrary(LIBRARY_NAME);
    }

    // Native -----------------------------------------------------------------

    public static native void nativeOnStart();

    public static native void nativeOnPause();

    public static native void nativeOnStop();

    public static native void nativeOnDestroy();

    public static native void nativeOnSurfaceCreated();

    public static native void nativeOnSurfaceChanged(int width, int height);

    public static native void nativeOnDrawFrame();

    public static native void nativeOnTouchesBegan(float pointX, float pointY);

    public static native void nativeOnTouchesEnded(float pointX, float pointY);

    public static native void nativeOnTouchesMoved(float pointX, float pointY);

    public static native void nativeOnTouchesBeganF(float pointX, float pointY, float pointX2, float pointY2);

    public static native void nativeOnTouchesMovedF(float pointX, float pointY, float pointX2, float pointY2);

    public static native void nativeLoadModel(String modelPath, float[] matrixArr);

    public static native void nativeStartMotion(String modelPath, float fadeInSeconds, float fadeOutSeconds);

    public static native void nativeStartLipSyncMotion(String modelPath, float fadeInSeconds, float fadeOutSeconds);

    public static native float[] nativeGetMatrixArray();


    // Java -----------------------------------------------------------------

    public static void setPlatformManager(IPlatform3Manager pm) {
        sIPlatform3Manager = pm;
    }

    /**
     * 加载资源，c调用java的方法
     *
     * @param filePath
     * @return
     */
    public static byte[] LoadFile(String filePath) {
        if (sIPlatform3Manager != null) {
            return sIPlatform3Manager.loadBytes(filePath);
        }
        return null;
        //Toast.makeText(sContext, "LoadFile:"+filePath, Toast.LENGTH_SHORT).show();
    }

    public static void hitTest(String action) {
        if (sIPlatform3Manager != null) {
            sIPlatform3Manager.hitTest(action);
        }
    }

    public static String getDefaultModelFile(String key) {
        //return "RURI/RURI.hitareas3.json";
        return sIPlatform3Manager != null ? sIPlatform3Manager.getModelFilePath(key) : null;
    }

}
