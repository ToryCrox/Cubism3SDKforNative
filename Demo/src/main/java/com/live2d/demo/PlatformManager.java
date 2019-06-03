package com.live2d.demo;

import android.content.Context;
import android.util.Log;

import com.mimikko.live2d3.IPlatform3Manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author tory
 * @date 2019/6/3
 * @des:
 */
public class PlatformManager implements IPlatform3Manager {
    private static final String TAG = "PlatformManager";

    private Context mContext;
    public PlatformManager(Context context){
        mContext = context.getApplicationContext();
    }

    @Override
    public byte[] loadBytes(String filePath) {
        if (filePath == null){
            return null;
        }
        InputStream is = null;
        try {
            if (filePath.startsWith("/")){
                is = new FileInputStream(filePath);
            } else {
                is = mContext.getAssets().open(filePath);
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

    @Override
    public void log(String txt) {

    }

    @Override
    public String getModelFilePath(String name) {
        return null;
    }

    @Override
    public void hitTest(String action) {

    }
}
