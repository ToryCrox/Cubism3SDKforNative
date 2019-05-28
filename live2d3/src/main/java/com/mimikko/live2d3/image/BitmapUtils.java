package com.mimikko.live2d3.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

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
    public static boolean isAvailable(Bitmap bitmap){
        return bitmap != null && !bitmap.isRecycled();
    }

    /**
     * recycler bitmap
     * @param bitmap
     */
    public static void safelyRecycle( Bitmap bitmap){
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
    public static Bitmap scaleCenterCrop2(Bitmap src, int targetWidth, int targetHeight){
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
        float targetScale = Math.min(scale, 1f);//目标图片可以缩小
        targetWidth = (int) (targetWidth * targetScale);
        targetHeight = (int) (targetHeight * targetScale);
        float dx = (srcWidth - targetWidth) / 2.0f;
        float dy = (srcHeight - targetHeight) / 2.0f;
        float srcScale = Math.max(1f, scale);
        Bitmap bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / srcScale, 1 / srcScale);//这里应用是1/scale才是缩放图片以适应原图大小
        //canvas.drawBitmap(src, -dx, -dy, paint); //这是指从坐标轴的哪里开始画
        canvas.translate( -dx, -dy);//此处是移动原点坐标为负值, 绘图的位置不变，只移动的画布
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.setBitmap(null);
        return bitmap;
    }

    public static Bitmap scaleCenterCrop( Bitmap src, int targetWidth, int targetHeight){
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
        canvas.translate( -dx, -dy);//此处是移动原点坐标为负值, 绘图的位置不变，只移动的画布
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.setBitmap(null);
        safelyRecycle(src);
        return bitmap;
    }


    /**
     * @param drawable
     * @return
     */
    private static Bitmap toBitmap( Drawable drawable, int width, int height, boolean setBound) {
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

    public static Bitmap toScaledBitmap( Drawable drawable, int targetWidth, int targetHeight){
        Drawable.ConstantState state = drawable.getConstantState();
        if (state == null){
            return null;
        }
        Drawable d = state.newDrawable();
        if (d instanceof ColorDrawable){
            return toBitmap(d,
                    1, 1, true);
        } else {
            Bitmap bitmap = toBitmap(d, d.getIntrinsicWidth(),
                    d.getIntrinsicHeight(), true);
            Bitmap result = scaleCenterCrop2(bitmap, targetWidth, targetHeight);
            safelyRecycle(bitmap);
            return result;
        }
    }
}