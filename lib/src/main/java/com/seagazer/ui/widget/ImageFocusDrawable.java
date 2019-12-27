package com.seagazer.ui.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * Default image drawable focus frame
 */
public class ImageFocusDrawable extends FocusDrawable {

    private Bitmap mImage;
    private int mWidth, mHeight;

    public ImageFocusDrawable(@NonNull Drawable drawable, int width, int height) {
        mWidth = width;
        mHeight = height;
        mImage = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ?
                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mImage);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
    }

    @Override
    void drawFocusFrame(Canvas canvas, Rect drawRect) {
        int x = (drawRect.right - drawRect.left - mWidth) / 2 + drawRect.left;
        int y = (drawRect.bottom - drawRect.top - mHeight) / 2 + drawRect.top;
        canvas.drawBitmap(mImage, x, y, null);
    }
}
