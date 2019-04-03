package com.live2d.demo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

import java.util.Arrays;

/**
 * @author tory
 * @date 2019/3/12
 * @des:
 */
public class Live2dView extends GLSurfaceView {

    private Live2dRenderer mRender;
    private Handler mHandler;
    private AccelHelper mAccelHelper;

    public Live2dView(Context context) {
        super(context);
        setFocusable(true);
        setZOrderOnTop(true);
        setEGLContextClientVersion(2);
        //设置透明
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);//为GLSurfaceView指定Alpha通道

        mRender = new Live2dRenderer();
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mHandler = new Handler(Looper.myLooper());
        mAccelHelper = new AccelHelper(context);
        mRender.setAccelHelper(mAccelHelper);
    }

    private Runnable resumeRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAccelHelper != null){
                mAccelHelper.start();
            }
        }
    };

    private Runnable pauseRunnable = new Runnable() {
        @Override
        public void run() {
            setVisibility(INVISIBLE);
            if (mAccelHelper != null){
                mAccelHelper.stop();
            }
        }
    };

    @Override
    public void onResume() {
        //super.onResume();
        setVisibility(VISIBLE);
        mHandler.removeCallbacks(resumeRunnable);
        mHandler.removeCallbacks(pauseRunnable);
        mHandler.postDelayed(resumeRunnable, 200);
    }

    @Override
    public void onPause() {
        //super.onPause();
        mHandler.removeCallbacks(resumeRunnable);
        mHandler.removeCallbacks(pauseRunnable);
        mHandler.postDelayed(pauseRunnable, 200);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getActionMasked()) {
            //case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN: {
                int n = event.getPointerCount();
                LogUtils.d("ACTION_DOWN n="+n);
                if (n == 1) {
                    JniBridgeJava.nativeOnTouchesBegan(pointX, pointY);
                } else if (n == 2) {
                    JniBridgeJava.nativeOnTouchesBeganF(event.getX(0), event.getY(0),
                            event.getX(1), event.getY(1));
                }
            }
            break;
            case MotionEvent.ACTION_UP:
                LogUtils.d("ACTION_UP pointX=" + pointX + ", pointY="+pointY);
                JniBridgeJava.nativeOnTouchesEnded(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE: {
                int n = event.getPointerCount();
                LogUtils.d("ACTION_MOVE n="+n);
                if (n == 1) {
                    JniBridgeJava.nativeOnTouchesMoved(pointX, pointY);
                } else if (n == 2) {
                    LogUtils.d("ACTION_MOVE .......");
                    JniBridgeJava.nativeOnTouchesMovedF(event.getX(0), event.getY(0),
                            event.getX(1), event.getY(1));
                }
            }
            break;
            case MotionEvent.ACTION_POINTER_UP:
                float[] viewMatrix = JniBridgeJava.nativeGetMatrixArray();
                LogUtils.d("ACTION_POINTER_UP .......array=" + Arrays.toString(viewMatrix));
                saveViewMatrix(viewMatrix);
                break;
        }
        return true;
    }

    private void saveViewMatrix(float[] viewMatrix) {
        Utils.saveViewMatrix(getContext(), viewMatrix);
    }
}
