package com.seagazer.lib.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A drawable wrapper to auto fill the vision with the max of width and height.
 * Set the {@link AlignMode} to clip the drawable, so it can fill this vision and keep the drawable ratio of width : height.
 */
public class RatioDrawableWrapper extends Drawable {
    private Drawable mDrawable;
    private int mWidth, mHeight;
    private AlignMode mAlignMode;
    private boolean isLandscape;
    private int mMaskColor = -1;

    /**
     * @param drawable the content drawable
     * @param mode     the mode to clip drawable, default is align start if not set this mode
     */
    public RatioDrawableWrapper(Drawable drawable, @Nullable AlignMode mode) {
        this.mDrawable = drawable;
        this.mAlignMode = mode == null ? AlignMode.START : mode;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // the size of canvas is the same as this wrapperDrawable's bounds
        float scaleW = mWidth * 1.0f / mDrawable.getIntrinsicWidth();
        float scaleH = mHeight * 1.0f / mDrawable.getIntrinsicHeight();
        float scale = isLandscape ? scaleW : scaleH;
        canvas.save();
        canvas.scale(scale, scale);
        if (mAlignMode == AlignMode.END) {
            if (isLandscape) {
                canvas.translate(0, (mHeight - mDrawable.getIntrinsicHeight() * scale) / scale);
            } else {
                canvas.translate((mWidth - mDrawable.getIntrinsicWidth() * scale) / scale, 0);
            }
        } else if (mAlignMode == AlignMode.CENTER) {
            if (isLandscape) {
                canvas.translate(0, (mHeight - mDrawable.getIntrinsicHeight() * scale) / scale / 2);
            } else {
                canvas.translate((mWidth - mDrawable.getIntrinsicWidth() * scale) / scale / 2, 0);
            }
        }
        mDrawable.draw(canvas);
        if (mMaskColor != -1) {
            canvas.drawColor(mMaskColor);
        }
        canvas.restore();
    }

    /**
     * Set a color mask overlay this drawable
     * @param color
     */
    public void setColorMask(@ColorInt int color) {
        this.mMaskColor = color;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);// this wrapper's bounds
        mWidth = right - left;// the width of drawer host
        mHeight = bottom - top;// the height of drawer host
        isLandscape = mWidth >= mHeight;
        int drawableWidth = mDrawable.getIntrinsicWidth();// the width of bitmap
        int drawableHeight = mDrawable.getIntrinsicHeight();// the height of bitmap
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