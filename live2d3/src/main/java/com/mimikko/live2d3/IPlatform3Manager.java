package com.mimikko.live2d3;

/**
 * @author tory
 * @date 2019/6/3
 * @des:
 */
public interface IPlatform3Manager {

    byte[] loadBytes(String path);
    void log(String txt);
    String getModelFilePath(String name);
    void hitTest(String action);
}
