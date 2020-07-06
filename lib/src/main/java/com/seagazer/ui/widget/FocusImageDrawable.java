package com.seagazer.ui.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * Default image drawable focus frame.
 */
public class FocusImageDrawable implements FocusLampDrawable {

    private Bitmap mImage;
    private int mWidth, mHeight;

    /**
     * @param drawable       The drawable to draw
     * @param drawableWidth  The width of this drawable
     * @param drawableHeight The height of this drawable
     */
    public FocusImageDrawable(@NonNull Drawable drawable, int drawableWidth, int drawableHeight) {
        mWidth = drawableWidth;
        mHeight = drawableHeight;
        mImage = Bitmap.createBitmap(drawableWidth, drawableHeight, drawable.getOpacity() != PixelFormat.OPAQUE ?
                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mImage);
        drawable.setBounds(0, 0, drawableWidth, drawableHeight);
        drawable.draw(canvas);
    }

    @Override
    public void drawFocusLamp(Canvas canvas, Rect focusRect) {
        int x = (focusRect.right - focusRect.left - mWidth) / 2 + focusRect.left;
        int y = (focusRect.bottom - focusRect.top - mHeight) / 2 + focusRect.top;
        canvas.drawBitmap(mImage, x, y, null);
    }
}
