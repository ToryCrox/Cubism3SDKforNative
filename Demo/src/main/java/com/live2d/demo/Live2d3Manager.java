package com.live2d.demo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.mimikko.live2d3.Live2d3Delegate;

/**
 * @author tory
 * @date 2019/3/12
 * @des:
 */
public class Live2d3Manager {
    private static final String TAG = "Live2dManager";
    public static int sMaxId = 1;

    private Live2d3View mView;
    private String mModelPath;
    private Live2d3ViewDelegate mViewDelegate;
    private Live2d3Delegate mLive2dDelegate;

    public Live2d3Manager(Context context) {
        mLive2dDelegate = new Live2d3Delegate(sMaxId ++);
        mViewDelegate = new Live2d3ViewDelegate(this, context);

    }

    public Live2d3Delegate getDelegate(){
        return mLive2dDelegate;
    }

    public Live2d3ViewDelegate getViewDelegate() {
        return mViewDelegate;
    }

    public void loadModel(String path) {
        mModelPath = path;
        try {
            mLive2dDelegate.loadModel(path, getViewMatrix());
        } catch (Exception e){
            Log.e(TAG, "loadModel: ", e);
        }
    }

    public void setReload() {
        loadModel(mModelPath);
    }

    public Live2d3View createView(Context context) {
        Live2d3View view = new Live2d3View(context);
        view.setDelegate(mViewDelegate);
        mView = view;
        return view;
    }

    public void setBackgroundImage(Drawable drawable){
        if (mViewDelegate.getImageHandler() != null){
            mViewDelegate.getImageHandler().setImage(drawable);
        }
    }

    public float[] getViewMatrix() {
        return null;
    }

    public void saveViewMatrix(float[] viewMatrix) {
    }

    public void update() {

    }

    public void onStart() {
        mViewDelegate.onStart();
    }

    public void onResume() {
        if (mView != null){
            mView.onResume();
        }
        mViewDelegate.onResume();
    }

    public void onPause() {
        if (mView != null){
            mView.onPause();
        }
        mViewDelegate.onPause();
    }

    public void onStop() {
        mViewDelegate.onStop();
    }

    public void onDestroy() {
        mViewDelegate.onDestroy();
    }

    public void hitTest(String action) {

    }
}
