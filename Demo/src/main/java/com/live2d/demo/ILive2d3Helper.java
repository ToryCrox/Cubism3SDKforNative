package com.live2d.demo;

import android.content.Context;

import java.io.InputStream;

/**
 * @author tory
 * @date 2019/4/8
 * @des:
 */
public interface ILive2d3Helper {
    public Context getAppContext();
    public InputStream decryptStream(InputStream in);
    public String getDefaultModelFile(String key);
}
