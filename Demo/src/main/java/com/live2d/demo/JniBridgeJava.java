/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.demo;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.live2d.demo.utils.FileManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Random;

public class JniBridgeJava {

    private static final String LIBRARY_NAME = "JniBridge";
    private static Activity _activityInstance;
    private static Context _context;

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
    public static native void nativeStartMotion(String modelPath, float pointX, float pointY);

    public static native float[] nativeGetMatrixArray();


    // Java -----------------------------------------------------------------

    public static void setContext(Context context) {
        _context = context;
    }

    public static void setActivityInstance(Activity activity) { _activityInstance = activity; }

    /**
     * 加载资源，c调用java的方法
     * @param filePath
     * @return
     */
    public static byte[] LoadFile(String filePath) {
        //Toast.makeText(_context, "LoadFile:"+filePath, Toast.LENGTH_SHORT).show();
        LogUtils.d("LoadFile="+filePath);
        if (filePath == null){
            return null;
        }
        InputStream is = null;
        try {
            if (filePath.startsWith("/")){
                is = new FileInputStream(filePath);
            } else {
                is = _context.getAssets().open(filePath);
            }
            int fileSize = is.available();
            byte[] fileBuffer = new byte[fileSize];
            is.read(fileBuffer, 0, fileSize);
            return fileBuffer;
        } catch(IOException e) {
            LogUtils.e("LoadFile="+filePath, e);
            return null;
        } finally {
            FileManager.closeSilently(is);
        }
    }

    public static void moveTaskToBack() {
        LogUtils.d("moveTaskToBack...");
        _activityInstance.moveTaskToBack(true);
    }

    public static void hitTest(String action){
        Toast.makeText(_context, "hitTest:"+ action, Toast.LENGTH_SHORT).show();
        LogUtils.d("hitTest="+action);
        String[] actions = new String[]{
                "motions/LL21605KJ.motion3.json",
                "motions/LL21606KJ.motion3.json",
                "motions/LL21701CJ.motion3.json",
                "motions/LL21702CJ.motion3.json",
                "motions/LL21703CJ.motion3.json",
                "motions/LL21704CJ.motion3.json",
                "motions/LL21705KJ.motion3.json",
                "motions/LL21706KJ.motion3.json",
        };
        int i = new Random().nextInt(actions.length);
        String motionPath = "RURI/" + actions[i];
        nativeStartMotion(motionPath, 1.0f, 1.0f);
    }

}
