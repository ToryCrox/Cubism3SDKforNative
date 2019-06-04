package com.mimikko.live2d3;

/**
 * @author tory
 * @date 2019/6/4
 * @des:
 */
public class Live2d3Delegate {

    private int mHandlerId;

    public Live2d3Delegate(int id) {
        mHandlerId = id;
    }

    public void onStart() {
        JniBridgeJava.nativeOnStart(mHandlerId);
    }

    public void onStop() {
        JniBridgeJava.nativeOnStop(mHandlerId);
    }

    public void onDestroy() {
        JniBridgeJava.nativeOnDestroy(mHandlerId);
    }


    public void onSurfaceCreated() {
        JniBridgeJava.nativeOnSurfaceCreated(mHandlerId);
    }

    public void onSurfaceChanged(int width, int height) {
        JniBridgeJava.nativeOnSurfaceChanged(mHandlerId, width, height);
    }

    public void onDrawFrame() {
        JniBridgeJava.nativeOnDrawFrame(mHandlerId);
    }

    public void onTouchesBegan(float pointX, float pointY) {
        JniBridgeJava.nativeOnTouchesBegan(mHandlerId, pointX, pointY);
    }

    public void onTouchesEnded(float pointX, float pointY) {
        JniBridgeJava.nativeOnTouchesEnded(mHandlerId, pointX, pointY);
    }

    public void onTouchesMoved(float pointX, float pointY) {
        JniBridgeJava.nativeOnTouchesMoved(mHandlerId, pointX, pointY);
    }

    public void onTouchesBeganF(float pointX, float pointY, float pointX2, float pointY2) {
        JniBridgeJava.nativeOnTouchesBeganF(mHandlerId, pointX, pointY, pointX2, pointY2);
    }

    public void onTouchesMovedF(float pointX, float pointY, float pointX2, float pointY2) {
        JniBridgeJava.nativeOnTouchesMovedF(mHandlerId, pointX, pointY, pointX2, pointY2);
    }

    public void loadModel(String modelPath, float[] matrixArr) {
        JniBridgeJava.nativeLoadModel(mHandlerId, modelPath, matrixArr);
    }

    public void startMotion(String modelPath, float fadeInSeconds, float fadeOutSeconds) {
        JniBridgeJava.nativeStartMotion(mHandlerId, modelPath, fadeInSeconds, fadeOutSeconds);
    }

    public void startLipSyncMotion(String modelPath, float fadeInSeconds, float fadeOutSeconds){
        JniBridgeJava.nativeStartLipSyncMotion(mHandlerId, modelPath, fadeInSeconds, fadeOutSeconds);
    }

    public void setModelScale(float scale){
        JniBridgeJava.nativeSetModelScale(mHandlerId, scale);
    }

    public float[] getMatrixArray(){
        return JniBridgeJava.nativeGetMatrixArray(mHandlerId);
    }

    public static void setAutoRandomMotion(boolean b){
        JniBridgeJava.nativeSetAutoRandomMotion(b);
    }

    public static void setDebugLog(boolean b){
        JniBridgeJava.nativeSetDebugLog(b);
    }


}
