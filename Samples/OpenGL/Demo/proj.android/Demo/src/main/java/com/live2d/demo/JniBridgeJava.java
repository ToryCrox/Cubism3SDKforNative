/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.demo;

import android.app.Activity;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

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
        InputStream fileData = null;
        try
        {
            fileData = _context.getAssets().open(filePath);
            int fileSize = fileData.available();
            byte[] fileBuffer = new byte[fileSize];
            fileData.read(fileBuffer, 0, fileSize);
            return fileBuffer;
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                if (fileData != null)
                {
                    fileData.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void moveTaskToBack() {
        _activityInstance.moveTaskToBack(true);
    }

}