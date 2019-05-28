/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.mimikko.live2d3;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class JniBridgeJava {
    private static final String TAG = "JniBridgeJava";

    private static final String LIBRARY_NAME = "JniBridge";
    private static Context sContext;
    private static WeakReference<Live2d3Manager> sManager;

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

    public static native float[] nativeGetMatrixArray();


    // Java -----------------------------------------------------------------

    public static void setContext(Context context) {
        sContext = context.getApplicationContext();
    }

    public static void setManager(Live2d3Manager manager) {
        sManager = new WeakReference<>(manager);
    }

    /**
     * 加载资源，c调用java的方法
     * @param filePath
     * @return
     */
    public static byte[] LoadFile(String filePath) {
        //Toast.makeText(sContext, "LoadFile:"+filePath, Toast.LENGTH_SHORT).show();
        if (filePath == null){
            return null;
        }
        InputStream is = null;
        try {
            if (filePath.startsWith("/")){
                is = new FileInputStream(filePath);
            } else {
                is = sContext.getAssets().open(filePath);
            }
            int fileSize = is.available();
            byte[] fileBuffer = new byte[fileSize];
            if (is.read(fileBuffer, 0, fileSize) != -1){
                return fileBuffer;
            } else {
                return null;
            }
        } catch(IOException e) {
            Log.e(TAG, "LoadFile: ", e);
            return null;
        } finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void hitTest(String action){
        Toast.makeText(sContext, "hitTest:"+ action, Toast.LENGTH_SHORT).show();
        Live2d3Manager manager = sManager.get();
        if (manager != null){
            manager.hitTest(action);
        }
    }


    public static String getDefaultModelFile(String key){
        //return "RURI/RURI.hitareas3.json";
        return null;
    }

}
