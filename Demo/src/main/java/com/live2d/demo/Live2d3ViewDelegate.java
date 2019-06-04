package com.live2d.demo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import com.live2d.demo.image.GL20ImageHandler;
import com.live2d.demo.image.GLImageHandler;

/**
 * @author tory
 * @date 2019/4/8
 * @des:
 */
public class Live2d3ViewDelegate {

    private Live2d3Renderer mRender;
    private Handler mHandler;
    private AccelHelper mAccelHelper;
    private VisibleCallback mCallback;
    private Live2d3Manager mManager;
    private GLImageHandler mImageHandler;

    public Live2d3ViewDelegate(Live2d3Manager manager, Context context){
        mManager = manager;
        mRender = new Live2d3Renderer(manager);
        mHandler = new Handler(Looper.getMainLooper());
        mAccelHelper = new AccelHelper(context);
        mRender.setAccelHelper(mAccelHelper);
        mImageHandler = new GL20ImageHandler();
        mRender.setImageHandler(mImageHandler);
    }

    public GLImageHandler getImageHandler() {
        return mImageHandler;
    }

    public Live2d3Renderer getRender() {
        return mRender;
    }

    public void setCallback(VisibleCallback callback) {
        mCallback = callback;
    }

    private Runnable mResumeRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAccelHelper != null){
                mAccelHelper.start();
            }
        }
    };

    private Runnable mPauseRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCallback != null){
                mCallback.call(View.INVISIBLE);
            }
            if (mAccelHelper != null){
                mAccelHelper.stop();
            }
        }
    };


    public void onResume() {
        if (mCallback != null){
            mCallback.call(View.INVISIBLE);
        }
        mHandler.removeCallbacks(mResumeRunnable);
        mHandler.removeCallbacks(mPauseRunnable);
        mHandler.postDelayed(mResumeRunnable, 200);
    }


    public void onPause() {
        mHandler.removeCallbacks(mResumeRunnable);
        mHandler.removeCallbacks(mPauseRunnable);
        mHandler.postDelayed(mPauseRunnable, 200);
    }

    public void onStop() {
        mManager.getDelegate().onStop();
    }


    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getActionMasked()) {
            //case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN: {
                int n = event.getPointerCount();
                if (n == 1) {
                    mManager.getDelegate().onTouchesBegan(pointX, pointY);
                } else if (n == 2) {
                    mManager.getDelegate().onTouchesBeganF(event.getX(0),
                            event.getY(0), event.getX(1),
                            event.getY(1));
                }
            }
            break;
            case MotionEvent.ACTION_UP:
                mManager.getDelegate().onTouchesEnded(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE: {
                int n = event.getPointerCount();
                if (n == 1) {
                    mManager.getDelegate().onTouchesMoved(pointX, pointY);
                } else if (n == 2) {
                    mManager.getDelegate().onTouchesMovedF(event.getX(0),
                            event.getY(0), event.getX(1),
                            event.getY(1));
                }
            }
            break;
            case MotionEvent.ACTION_POINTER_UP:
                float[] viewMatrix = mManager.getDelegate().getMatrixArray();
                mManager.saveViewMatrix(viewMatrix);
                break;
        }
        return true;
    }

    public void setReload() {
        mManager.setReload();
    }

    public void onDestroy() {
        mManager.getDelegate().onDestroy();
    }

    public void onStart() {
        mManager.getDelegate().onStart();
    }

    public interface VisibleCallback{
        void call(int visibility);
    }
}
