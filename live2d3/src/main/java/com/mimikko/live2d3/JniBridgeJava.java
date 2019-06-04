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

    static native void nativeOnStart(int id);

    static native void nativeOnStop(int id);

    static native void nativeOnDestroy(int id);

    static native void nativeOnSurfaceCreated(int id);

    static native void nativeOnSurfaceChanged(int id,int width, int height);

    static native void nativeOnDrawFrame(int id);

    static native void nativeOnTouchesBegan(int id,float pointX, float pointY);

    static native void nativeOnTouchesEnded(int id,float pointX, float pointY);

    static native void nativeOnTouchesMoved(int id,float pointX, float pointY);

    static native void nativeOnTouchesBeganF(int id,float pointX, float pointY, float pointX2, float pointY2);

    static native void nativeOnTouchesMovedF(int id,float pointX, float pointY, float pointX2, float pointY2);

    static native void nativeLoadModel(int id,String modelPath, float[] matrixArr);

    static native void nativeStartMotion(int id,String modelPath, float fadeInSeconds, float fadeOutSeconds);

    static native void nativeStartLipSyncMotion(int id,String modelPath, float fadeInSeconds, float fadeOutSeconds);

    static native float[] nativeGetMatrixArray(int id);

    static native void nativeSetAutoRandomMotion(boolean b);
    static native void nativeSetDebugLog(boolean b);


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
