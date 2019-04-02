package com.live2d.demo;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.view.View;

/**
 * @author tory
 * @date 2019/3/12
 * @des:
 */
public class Live2dManager implements LifecycleObserver {

    private Live2dView mView;
    private String mModelPath;

    public Live2dManager(Activity context) {
        JniBridgeJava.setActivityInstance(context);
        JniBridgeJava.setContext(context);
        mView = createView(context);
    }

    public void loadModel(String path) {
        mModelPath = path;
        JniBridgeJava.nativeRoadModel(mModelPath);
    }

    private Live2dView createView(Activity context) {
        return new Live2dView(context);
    }

    public View getLive2dView(){
        return mView;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
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
