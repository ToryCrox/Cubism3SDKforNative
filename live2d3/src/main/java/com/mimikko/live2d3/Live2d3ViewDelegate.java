package com.mimikko.live2d3;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

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

    public Live2d3ViewDelegate(Live2d3Manager manager, Context context){
        mManager = manager;
        mRender = new Live2d3Renderer();
        mHandler = new Handler(Looper.getMainLooper());
        mAccelHelper = new AccelHelper(context);
        mRender.setAccelHelper(mAccelHelper);
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


    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getActionMasked()) {
            //case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN: {
                int n = event.getPointerCount();
                if (n == 1) {
                    JniBridgeJava.nativeOnTouchesBegan(pointX, pointY);
                } else if (n == 2) {
                    JniBridgeJava.nativeOnTouchesBeganF(event.getX(0),
                            event.getY(0), event.getX(1),
                            event.getY(1));
                }
            }
            break;
            case MotionEvent.ACTION_UP:
                JniBridgeJava.nativeOnTouchesEnded(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE: {
                int n = event.getPointerCount();
                if (n == 1) {
                    JniBridgeJava.nativeOnTouchesMoved(pointX, pointY);
                } else if (n == 2) {
                    JniBridgeJava.nativeOnTouchesMovedF(event.getX(0),
                            event.getY(0), event.getX(1),
                            event.getY(1));
                }
            }
            break;
            case MotionEvent.ACTION_POINTER_UP:
                float[] viewMatrix = JniBridgeJava.nativeGetMatrixArray();
                mManager.saveViewMatrix(viewMatrix);
                break;
        }
        return true;
    }

    public void setReload() {
        mManager.setReload();
    }

    public interface VisibleCallback{
        void call(int visibility);
    }
}
