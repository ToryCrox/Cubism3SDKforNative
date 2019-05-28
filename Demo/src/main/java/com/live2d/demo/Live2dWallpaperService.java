package com.live2d.demo;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.aleaf.android.glwallpaperservice.GLWallpaperService;
import com.mimikko.live2d3.Live2d3ViewDelegate;

/**
 * @author tory
 * @date 2019/4/24
 * @des:
 */
public class Live2dWallpaperService extends GLWallpaperService {
    private final String TAG = "Live2dWallpaperService#"+hashCode();


    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }

    private class MyEngine extends GLEngine{

        Live2dDemoManager mLive2dManager;
        Live2d3ViewDelegate mDelegate;

        private MyEngine(){
            super();
            setEGLContextClientVersion(2);
            //设置透明
            setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            Context context = getApplicationContext();
            mLive2dManager = new Live2dDemoManager(context);
            mDelegate = mLive2dManager.getViewDelegate();
            setRenderer(mDelegate.getRender());
            mLive2dManager.setBackgroundImage(ContextCompat.getDrawable(context, R.drawable.bg_pic_test2));
            mLive2dManager.loadModel("RURI/RURI.model3.json");
            LogUtils.d(TAG, "MyEngine");
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            LogUtils.d(TAG, "onCreate");
        }

        @Override
        public void onResume() {
            //super.onResume();
            mLive2dManager.onStart();
            mLive2dManager.onResume();
            LogUtils.d(TAG, "onResume");
        }

        @Override
        public void onPause() {
            //super.onPause();
            mLive2dManager.onPause();
            mLive2dManager.onStop();
            LogUtils.d(TAG, "onPause");
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            mDelegate.onTouchEvent(event);
            LogUtils.d(TAG, "onTouchEvent");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mLive2dManager.onDestroy();
            LogUtils.d(TAG, "onDestroy");

        }
    }
}
