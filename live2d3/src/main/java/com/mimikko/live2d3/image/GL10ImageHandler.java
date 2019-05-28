package com.mimikko.live2d3.image;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author tory
 * @date 2019/4/23
 * @des:
 */
public class GL10ImageHandler extends BaseGLImageHandler {

    private static final String TAG = "GL10ImageHandler";


    private static float TEXTURE[] = {
            // Mapping coordinates for the VERTICES
            0.0f, 1.0f, // top left (V2)
            0.0f, 0.0f, // bottom left (V1)
            1.0f, 1.0f, // top right (V4)
            1.0f, 0.0f // bottom right (V3)
    };

    private static float VERTICES[] = {
            -1.0f, -1.0f, 0.0f, // V1 - bottom left
            -1.0f, 1.0f, 0.0f, // V2 - top left
            1.0f, -1.0f, 0.0f, // V3 - bottom right
            1.0f, 1.0f, 0.0f // V4 - top right
    };

    private FloatBuffer mTextureBuffer; // buffer holding the TEXTURE coordinates
    private FloatBuffer mVertexBuffer; // buffer holding the VERTICES


    public GL10ImageHandler() {
        super();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(VERTICES.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuffer.asFloatBuffer();
        mVertexBuffer.put(VERTICES);
        mVertexBuffer.position(0);

        byteBuffer = ByteBuffer.allocateDirect(TEXTURE.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mTextureBuffer = byteBuffer.asFloatBuffer();
        mTextureBuffer.put(TEXTURE);
        mTextureBuffer.position(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        adjustImageScaling(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (mGLTextureId != NO_TEXTURE){
            onDraw(gl, mGLTextureId);
        }
    }

    @Override
    protected void adjustImageScaling(int imageWidth, int imageHeight) {
        super.adjustImageScaling(imageWidth, imageHeight);
        float scale = mOutputHeight * 1.0f / mOutputWidth;

        Log.d(TAG, "adjustImageScaling: scale="+scale);
        float[] vertices = new float[]{
                VERTICES[0], VERTICES[1] * scale, VERTICES[2],
                VERTICES[3], VERTICES[4] * scale, VERTICES[5],
                VERTICES[6], VERTICES[7] * scale, VERTICES[8],
                VERTICES[9], VERTICES[10] * scale, VERTICES[11]
        };
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    public void onDraw(GL10 gl, final int textureId) {
        // 传入的图片纹理
        if (textureId != NO_TEXTURE) {
            Log.d(TAG, "onDraw: ");
            gl.glLoadIdentity();
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

            // Point to our buffers
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            // Set the face rotation
            gl.glFrontFace(GL10.GL_CW);

            // Point to our vertex buffer
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);

            // Draw the VERTICES as triangle strip
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, VERTICES.length / 3);

            // Disable the client state before leaving
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        }
    }

    public int loadTexture(GL10 gl,final Bitmap img, final int usedTexId, final boolean recycle) {
        int textures[] = new int[]{-1};
        Log.d(TAG, "loadTexture: textureId="+usedTexId);

        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        // create nearest filtered TEXTURE
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, img, 0);

        if (recycle) {
            img.recycle();
        }
        Log.d(TAG, "loadTexture: end textureId="+textures[0]);
        return textures[0];
    }
}
