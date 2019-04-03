package com.live2d.demo;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.view.View;

import java.util.Arrays;

/**
 * @author tory
 * @date 2019/3/12
 * @des:
 */
public class Live2dManager implements LifecycleObserver {
    private static final String TAG = "Live2dManager";

    private Live2dView mView;
    private String mModelPath;

    public Live2dManager(Activity context) {
        JniBridgeJava.setActivityInstance(context);
        JniBridgeJava.setContext(context);
        mView = createView(context);
    }

    public void loadModel(String path) {
        mModelPath = path;
        float[] array = Utils.getViewMatrix(mView.getContext());
        LogUtils.d( "loadModel: path="+path + ", array="+ Arrays.toString(array));
        JniBridgeJava.nativeLoadModel(mModelPath, array);
    }

    private Live2dView createView(Activity context) {
        return new Live2dView(context);
    }

    public View getLive2dView(){
        return mView;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        LogUtils.d( "onStart: " + hashCode());
        JniBridgeJava.nativeOnStart();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        mView.onResume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        mView.onPause();
        JniBridgeJava.nativeOnPause();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        JniBridgeJava.nativeOnStop();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        JniBridgeJava.nativeOnDestroy();

    }

}
