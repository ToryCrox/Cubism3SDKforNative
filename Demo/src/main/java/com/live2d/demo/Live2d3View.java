package com.live2d.demo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * @author tory
 * @date 2019/3/12
 * @des:
 */
public class Live2d3View extends GLSurfaceView {

    private Live2d3ViewDelegate mDelegate;
    private boolean mHasDetachedFromWindow;

    public Live2d3View(Context context) {
        super(context);
        setFocusable(true);
        setZOrderOnTop(true);
        setEGLContextClientVersion(2);
        //设置透明
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);//为GLSurfaceView指定Alpha通道
    }

    public void setDelegate( Live2d3ViewDelegate delegate){
        mDelegate = delegate;
        setRenderer(delegate.getRender());
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDelegate.onTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mHasDetachedFromWindow){
            mHasDetachedFromWindow = false;
            if (mDelegate != null){
                mDelegate.setReload();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHasDetachedFromWindow = true;
    }
}
