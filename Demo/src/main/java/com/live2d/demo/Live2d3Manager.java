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
public class Live2d3Manager implements LifecycleObserver {
    private static final String TAG = "Live2dManager";

    private Live2d3View mView;
    private String mModelPath;
    private Live2d3ViewDelegate mViewDelegate;

    public Live2d3Manager(Activity context) {
        JniBridgeJava.setContext(context);
        mViewDelegate = new Live2d3ViewDelegate(this, context);
        mView = createView(context);
    }

    public void loadModel(String path) {
        mModelPath = path;
        float[] array = Utils.getViewMatrix(mView.getContext());
        LogUtils.d( "loadModel: path="+path + ", array="+ Arrays.toString(array));
        try {
            JniBridgeJava.nativeLoadModel(mModelPath, array);
        } catch (Exception e){
            LogUtils.e("loadModel: ", e);
        }

    }

    public void setReload() {
        loadModel(mModelPath);
    }

    private Live2d3View createView(Activity context) {
        Live2d3View view = new Live2d3View(context);
        view.setDelegate(mViewDelegate);
        return view;
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

    public void saveViewMatrix(float[] viewMatrix) {
    }

    public void update() {

    }
}
