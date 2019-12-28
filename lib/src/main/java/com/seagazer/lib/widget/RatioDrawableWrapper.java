package com.seagazer.lib.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A drawable wrapper to keep ratio for the drawer's width : height.
 * It always set the top as base line.
 */
public class RatioDrawableWrapper extends Drawable {
    private Drawable mDrawable;
    private int mWidth, mHeight;
    private AlignMode mAlignMode;

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
     * @param mode     the base line start to clip, default is align top if not set this mode
     */
    RatioDrawableWrapper(Drawable drawable, @Nullable AlignMode mode) {
        this.mDrawable = drawable;
        this.mAlignMode = mode == null ? AlignMode.TOP : mode;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // The canvas size is the same as this wrapperDrawable's bounds
        float scale = mWidth * 1.0f / mDrawable.getIntrinsicWidth();
        canvas.save();
        canvas.scale(scale, scale);
        if (mAlignMode == AlignMode.BOTTOM) {
            canvas.translate(0, (mHeight - mDrawable.getIntrinsicHeight() * scale) / scale);
        }
        mDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);// This wrapper's bounds
        mWidth = right - left;// The drawer host's width
        mHeight = bottom - top;// The drawer host's height
        int drawableWidth = mDrawable.getIntrinsicWidth();// The bitmap's width: px * density
        int drawableHeight = mDrawable.getIntrinsicHeight();// The bitmap's height: px * density
        mDrawable.setBounds(0, 0, drawableWidth, drawableHeight);
    }

    @Override
    public void setAlpha(int alpha) {
        mDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mDrawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return mDrawable.getOpacity();
    }

}