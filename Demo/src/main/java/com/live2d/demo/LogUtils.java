package com.live2d.demo;

import android.util.Log;

/**
 * @author tory
 * @date 2019/3/8
 * @des:
 */
public class LogUtils {
    private static final String TAG = "LIVE2D_DEMO";


    public static void d(String msg){
        Log.d(TAG, msg);
    }

    public static void d(String tag, String msg){
        Log.d(TAG, tag + " " +msg);
    }

    public static void e(String msg, Throwable e){
        Log.e(TAG, msg, e);
    }

    public static void e(String tag, String msg){
        Log.e(TAG,tag + " " + msg);
    }

    public static void e(String tag, String msg, Throwable e){
        Log.e(TAG,tag + " " + msg, e);
    }
}
