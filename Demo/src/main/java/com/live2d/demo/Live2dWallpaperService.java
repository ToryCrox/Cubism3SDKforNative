package com.live2d.demo;

import android.content.Context;

import com.live2d.demo.glwpservice.GLWallpaperService;

/**
 * @author tory
 * @date 2019/4/24
 * @des:
 */
public class Live2dWallpaperService extends GLWallpaperService {


    @Override
    public Engine onCreateEngine() {
        return new GLEngine();
    }

    private class MyEngine extends GLEngine{

        Live2dDemoManager mLive2dManager;

        private MyEngine(){
            super();
            Context context = getApplicationContext();
            mLive2dManager = new Live2dDemoManager(context);
        }


    }
}
