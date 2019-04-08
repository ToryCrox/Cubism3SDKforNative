package com.live2d.demo;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;

import com.mimikko.live2d3.Live2d3Manager;

/**
 * @author tory
 * @date 2019/4/8
 * @des:
 */
public class Live2dDemoManager extends Live2d3Manager implements LifecycleObserver {

    Context mContext;

    public Live2dDemoManager(Activity context) {
        super(context);
        mContext = context;
    }

    @Override
    public float[] getViewMatrix() {
        return Utils.getViewMatrix(mContext);
    }

    @Override
    public void saveViewMatrix(float[] viewMatrix) {
        Utils.saveViewMatrix(mContext, viewMatrix);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        LogUtils.d( "onStart: " + hashCode());
        super.onStart();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        super.onResume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        super.onPause();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        super.onStop();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        super.onDestroy();

    }
}
