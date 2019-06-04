package com.live2d.demo.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.mimikko.mimikkoui.toolkit.log.LogUtils;
import com.mimikko.mimikkoui.toolkit.utils.TimeRecorder;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author tory
 * @date 2019/4/23
 * @des:
 */
public class BaseGLImageHandler implements GLImageHandler {


    private static final String TAG = "BaseGLImageHandler";
    public static final int NO_TEXTURE = -1;


    protected FloatBuffer mGLCubeBuffer;
    protected FloatBuffer mGLTextureBuffer;

    protected int mGLTextureId = NO_TEXTURE; // 纹理id

    protected int mOutputWidth, mOutputHeight; // 窗口大小
    private Drawable mImage;

    public BaseGLImageHandler() {
    }


    private void tryLoadBitmap(GL10 gl) {
        if (mImage != null) {
            TimeRecorder.begin("loadTexture");
            Bitmap bmp = BitmapUtils.toScaledBitmap(mImage, mOutputWidth, mOutputHeight);
            if (bmp == null){
                return;
            }
            int imageWidth = bmp.getWidth();
            int imageHeight = bmp.getHeight();
            mGLTextureId = loadTexture(gl, bmp, mGLTextureId, true);
            adjustImageScaling(imageWidth, imageHeight);
            TimeRecorder.end("loadTexture");
            mImage = null;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mOutputWidth = width;
        mOutputHeight = height;
        LogUtils.d(TAG, "onSurfaceChanged: ");
        tryLoadBitmap(gl);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        tryLoadBitmap(gl);
    }

    // 调整图片显示大小为居中显示
    protected void adjustImageScaling(int imageWidth, int imageHeight) {

    }

    @Override
    public void setImage(Drawable image) {
        mImage = image;
    }

    @Override
    public int loadTexture(GL10 gl, Bitmap img, int usedTexId, boolean recycle) {
        return NO_TEXTURE;
    }
}
