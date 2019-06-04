package com.live2d.demo.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author tory
 * @create 2018/7/22
 * @Describe
 */
public class BitmapUtils {


    /**
     * 是否为合法的bitmap
     * @param bitmap
     * @return
     */
    public static boolean isAvailable(@Nullable Bitmap bitmap){
        return bitmap != null && !bitmap.isRecycled();
    }

    /**
     * recycler bitmap
     * @param bitmap
     */
    public static void safelyRecycle(@Nullable Bitmap bitmap){
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
        }
    }

    /**
     * 居中缩放并裁剪图片
     * 图片只缩小不放大
     * @param src
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    @Nullable
    public static Bitmap scaleCenterCrop2(@Nullable Bitmap src, int targetWidth, int targetHeight, boolean recycle){
        if (!isAvailable(src)){
            return null;
        }

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        if(srcWidth * 1.0f / srcHeight == targetWidth * 1.0f / targetHeight){
            return src;
        }

        //取小的缩放比例，这样画布缩放后才能有一边比原图小
        float scaleW = srcWidth * 1.0f / targetWidth;
        float scaleH = srcHeight * 1.0f / targetHeight;
        float scale = Math.min(scaleW, scaleH);
        float targetScale = Math.min(scale, 1f);
        return scaleCenterCrop(src, (int)(targetWidth * targetScale),
                (int)(targetHeight * targetScale));
    }

    /**
     * 居中缩放并裁剪图片
     * @param src
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    @Nullable
    public static Bitmap scaleCenterCrop(@Nullable Bitmap src, int targetWidth, int targetHeight, boolean recycle){
        if (!isAvailable(src)){
            return null;
        }

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        if(srcWidth == targetWidth && srcHeight == targetHeight){
            return src;
        }

        //取小的缩放比例，这样画布缩放后才能有一边比原图小
        float scaleW = srcWidth * 1.0f / targetWidth;
        float scaleH = srcHeight * 1.0f / targetHeight;
        float scale = Math.min(scaleW, scaleH);
        float dx = (srcWidth - targetWidth * scale) / 2;
        float dy = (srcHeight - targetHeight * scale) / 2;
        Bitmap bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / scale, 1 / scale);//这里应用是1/scale才是缩放图片以适应原图大小
        //canvas.drawBitmap(src, -dx, -dy, paint); //这是指从坐标轴的哪里开始画
        canvas.translate( -dx, 0);//此处是移动原点坐标为负值, 绘图的位置不变，只移动的画布
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.setBitmap(null);
        if (recycle){
            safelyRecycle(src);
        }
        return bitmap;
    }

    @Nullable
    public static Bitmap scaleCenterCrop(@Nullable Bitmap src, int targetWidth,
                                         int targetHeight){
        return scaleCenterCrop(src, targetWidth, targetHeight, false);
    }


    /**
     * @param drawable
     * @return
     */
    @NonNull
    private static Bitmap toBitmap(@NonNull Drawable drawable, int width, int height, boolean setBound) {
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?
                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;// 取 drawable 的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应 bitmap
        Canvas canvas = new Canvas(bitmap); // 建立对应 bitmap 的画布
        if (setBound){
            drawable.setBounds(0, 0, width, height);
        }
        drawable.draw(canvas);      // 把 drawable 内容画到画布中
        return bitmap;
    }

    public static Bitmap toScaledBitmap(@NonNull Drawable drawable, int targetWidth, int targetHeight){
        Drawable.ConstantState state = drawable.getConstantState();
        if (state == null){
            return null;
        }
        Drawable d = state.newDrawable();
        if (d instanceof ColorDrawable){
            return toBitmap(d,
                    1, 1, true);
        } else {
            Bitmap bitmap;
            if (d instanceof LayerDrawable || d instanceof NinePatchDrawable){
                bitmap = toBitmap(d, targetWidth, targetHeight, true);
            } else {
                bitmap = toBitmap(d, d.getIntrinsicWidth(),
                        d.getIntrinsicHeight(), true);
            }
            return scaleCenterCrop2(bitmap, targetWidth, targetHeight, true);
        }
    }

}