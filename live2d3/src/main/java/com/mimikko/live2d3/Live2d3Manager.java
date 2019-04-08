package com.mimikko.live2d3;

import android.app.Activity;
import android.view.View;

/**
 * @author tory
 * @date 2019/3/12
 * @des:
 */
public class Live2d3Manager {
    private static final String TAG = "Live2dManager";

    private Live2d3View mView;
    private String mModelPath;
    private Live2d3ViewDelegate mViewDelegate;

    public Live2d3Manager(Activity context) {
        JniBridgeJava.setContext(context);
        JniBridgeJava.setManager(this);
        mViewDelegate = new Live2d3ViewDelegate(this, context);
        mView = createView(context);
    }

    public void loadModel(String path) {
        mModelPath = path;
        try {
            JniBridgeJava.nativeLoadModel(path, getViewMatrix());
        } catch (Exception e){
            e.printStackTrace();
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

    public float[] getViewMatrix() {
        return null;
    }

    public void saveViewMatrix(float[] viewMatrix) {
    }

    public void update() {

    }

    public void onStart() {
        JniBridgeJava.nativeOnStart();
    }

    public void onResume() {
        mView.onResume();
    }

    public void onPause() {
        mView.onPause();
        JniBridgeJava.nativeOnPause();
    }

    public void onStop() {
        JniBridgeJava.nativeOnStop();
    }

    public void onDestroy() {
        JniBridgeJava.nativeOnDestroy();
    }

    public void hitTest(String action) {

    }
}
