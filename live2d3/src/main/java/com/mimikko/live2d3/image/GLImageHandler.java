package com.mimikko.live2d3.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author tory
 * @date 2019/4/23
 * @des:
 */
public interface GLImageHandler extends GLSurfaceView.Renderer {

    void setImage(Drawable image);
    int loadTexture(GL10 gl, final Bitmap img, final int usedTexId, final boolean recycle);
}
