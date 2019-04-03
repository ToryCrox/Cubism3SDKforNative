package com.live2d.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * @author tory
 * @date 2019/4/3
 * @des:
 */
public class Utils {

    public static final String KEY_MATRIX_ARR = "key_matrix_arr";


    private static SharedPreferences getPrefs(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void saveViewMatrix(Context context, float[] arr) {
        String value = null;
        if (arr != null){
            String[] vals = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                vals[i] = String.valueOf(arr[i]);
            }
            value = TextUtils.join(",", vals);
        }
        getPrefs(context).edit()
                .putString(KEY_MATRIX_ARR, value)
                .apply();
    }

    public static float[] getViewMatrix(Context context){
        String value = getPrefs(context).getString(KEY_MATRIX_ARR, null);
        if (value == null){
            return null;
        }
        String[] ps = value.split(",");
        float[] fs = new float[ps.length];
        for (int i = 0; i < ps.length; i++) {
            fs[i] = Float.valueOf(ps[i]);
        }
        return fs;
    }
}
