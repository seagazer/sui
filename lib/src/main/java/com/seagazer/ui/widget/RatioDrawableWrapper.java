package com.seagazer.ui.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.seagazer.ui.util.Logger;

/**
 * A drawable wrapper to keep ratio for the drawer's width : height.
 * It always set the top as base line.
 */
public class RatioDrawableWrapper extends Drawable {
    private Drawable drawable;
    private int boundWidth, boundHeight;
    private AlignMode mode;

    /**
     * Clip mode
     */
    public enum AlignMode {
        /**
         * clip from top
         */
        TOP,
        /**
         * clip from bottom
         */
        BOTTOM
    }

    /**
     * @param drawable the content drawable
     * @param mode     the base line to clip, default is align top
     */
    RatioDrawableWrapper(Drawable drawable, @Nullable AlignMode mode) {
        this.drawable = drawable;
        this.mode = mode == null ? AlignMode.TOP : mode;
    }

    // TODO: 2019/12/28 fix translation mode
    @Override
    public void draw(@NonNull Canvas canvas) {
        // The canvas size is the same as this wrapperDrawable's bounds
        float widthRatio = boundWidth * 1.0f / drawable.getIntrinsicWidth();
        float heightRatio = boundHeight * 1.0f / drawable.getIntrinsicHeight();
        float scale = Math.max(widthRatio, heightRatio);
        canvas.save();
        canvas.scale(scale, scale);
        Logger.d("drawableHeight=" + drawable.getIntrinsicHeight());
        Logger.d("boundHeight=" + boundHeight);
        Logger.d("hRatio=" + drawable.getIntrinsicHeight() * 1.0f / boundHeight);
        Logger.d("scale=" + scale);
        Logger.d("dy=" + Math.abs(boundHeight - drawable.getIntrinsicHeight()));
        if (mode == AlignMode.BOTTOM) {
            float translationY = drawable.getIntrinsicHeight() * 1.0f / boundHeight * scale * (boundHeight - drawable.getIntrinsicHeight());
            Logger.d("translationY=" + translationY);
            canvas.translate(0, translationY);
        }
        drawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);// This wrapper's bounds
        boundWidth = right - left;// The drawer host's width
        boundHeight = bottom - top;// The drawer host's height
        int drawableWidth = drawable.getIntrinsicWidth();// The bitmap's width: px * density
        int drawableHeight = drawable.getIntrinsicHeight();// The bitmap's height: px * density
        drawable.setBounds(0, 0, drawableWidth, drawableHeight);
    }

    @Override
    public void setAlpha(int alpha) {
        drawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        drawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return drawable.getOpacity();
    }

}