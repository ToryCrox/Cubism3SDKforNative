/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */
package com.live2d.demo.utils;

import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileManager {
    private static final String TAG = "FileManager";

    public static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception e) {
                Log.e(TAG, "closeSilently", e);
            }
        }
    }

    public static String getJoinedPath(String... paths) {
        if (paths == null || paths.length == 0) {
            return null;
        }
        File file = new File(paths[0]);
        for (int i = 1; i < paths.length; i++) {
            file = new File(file, paths[i]);
        }
        return file.getAbsolutePath();
    }

    public static boolean isExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        } else {
            return true;
        }
    }
}
