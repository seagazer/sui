package com.seagazer.sui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A drawable that is intended to cross-fade when change the drawable resource just like
 * {@link android.graphics.drawable.TransitionDrawable}.
 * <p>
 * Call {@link #fadeChange(Drawable, int)} to change the resource.
 */
public class CrossFadeDrawable extends Drawable {
    private Drawable mFront;
    private Drawable mBackground;
    private ValueAnimator mAnimator;
    private float mAnimPosition;

    public CrossFadeDrawable() {
        initAnimator();
    }

    private void initAnimator() {
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimPosition = (float) animation.getAnimatedValue();
                if (mFront != null) {
                    mFront.setAlpha((int) (255 * (1 - mAnimPosition)));
                }
                if (mBackground != null) {
                    mBackground.setAlpha((int) (255 * mAnimPosition));
                }
                invalidateSelf();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFront = mBackground;
                mFront.setAlpha(255);
                mBackground = null;
                invalidateSelf();
            }
        });
    }

    public void initDrawable(@NonNull Drawable drawable) {
        mFront = drawable;
        mFront.setBounds(getBounds());
        invalidateSelf();
    }

    /**
     * Change the drawable resource with cross-fade
     *
     * @param drawable the drawable resource
     * @param duration the duration of cross-fade animation
     */
    public void fadeChange(Drawable drawable, int duration) {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        mBackground = drawable;
        mBackground.setBounds(getBounds());
        mBackground.setAlpha(0);
        mAnimator.setDuration(duration);
        mAnimator.start();
    }

    /**
     * Release the drawable
     */
    public void release() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mFront != null) {
            mFront.draw(canvas);
        }
        if (mBackground != null) {
            mBackground.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (mFront != null) {
            mFront.setAlpha(alpha);
        }
        if (mBackground != null) {
            mBackground.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (mFront != null) {
            mFront.setColorFilter(colorFilter);
        }
        if (mBackground != null) {
            mBackground.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
