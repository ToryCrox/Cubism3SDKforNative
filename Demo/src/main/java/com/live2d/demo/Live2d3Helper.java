package com.live2d.demo;

import android.content.Context;

import java.io.InputStream;

/**
 * @author tory
 * @date 2019/4/8
 * @des:
 */
public class Live2d3Helper implements ILive2d3Helper {


    private static Live2d3Helper sInstance = new Live2d3Helper();

    public static Live2d3Helper getInstance(){
        return sInstance;
    }

    private ILive2d3Helper mImpl;

    public void setImpl(ILive2d3Helper helper){
        mImpl = helper;
    }


    @Override
    public Context getAppContext() {
        return mImpl != null ? mImpl.getAppContext() : null;
    }

    @Override
    public InputStream decryptStream(InputStream in) {
        if (mImpl != null){
            return mImpl.decryptStream(in);
        }
        return in;
    }

    @Override
    public String getDefaultModelFile(String key) {
        return null;
    }
}
