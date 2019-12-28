package com.seagazer.lib.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;

public abstract class FocusAnimContainer extends FrameLayout implements ViewTreeObserver.OnGlobalFocusChangeListener {
    private View mFocusFrame;
    private AnimatorSet mAnim;
    private View mNewFocus;
    private AnimRunnable mAnimRunnable;
    private final Handler mKeyHandler = new Handler();
    private boolean isAnimEnable = false;
    private float mScale = 1;

    public FocusAnimContainer(Context context) {
        this(context, null);
    }

    public FocusAnimContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusAnimContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFocusFrame = new View(context);
        mFocusFrame.setBackground(getFocusFrame());
        mFocusFrame.setVisibility(INVISIBLE);
        mAnimRunnable = new AnimRunnable();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mKeyHandler.hasMessages(0)) {
                return true;
            }
            mKeyHandler.sendEmptyMessageDelayed(0, 250);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(mFocusFrame, new FrameLayout.LayoutParams(1, 1));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnim();
        getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
        mKeyHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (!isAnimEnable) {
            return;
        }
        if (newFocus != null) {
            mNewFocus = newFocus;
            if (mFocusFrame != null) {
                if (checkFocusInstance(newFocus)) {
                    removeCallbacks(mAnimRunnable);
                    post(mAnimRunnable);
                } else {
                    mFocusFrame.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private class AnimRunnable implements Runnable {

        @Override
        public void run() {
            if (mNewFocus == null) {
                return;
            }
            cancelAnim();
            int targetW = mNewFocus.getWidth();
            int targetH = mNewFocus.getHeight();
            int[] newLoc = new int[2];
            mNewFocus.getLocationOnScreen(newLoc);
            final LayoutParams newLp = new LayoutParams((int) (targetW * mScale), (int) (targetH * mScale));
            final float newX = newLoc[0] - targetW * (mScale - 1) / 2 - getPaddingStart();
            final float newY = newLoc[1] - targetH * (mScale - 1) / 2 - getPaddingTop();
            if (mFocusFrame.getVisibility() != VISIBLE) {
                mFocusFrame.setLayoutParams(newLp);
                mFocusFrame.setX(newX);
                mFocusFrame.setY(newY);
                mFocusFrame.setVisibility(VISIBLE);
            } else {
                ValueAnimator lp = ValueAnimator.ofObject(mEvaluator, mFocusFrame.getLayoutParams(), newLp);
                lp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        FrameLayout.LayoutParams lp = (LayoutParams) animation.getAnimatedValue();
                        mFocusFrame.setLayoutParams(lp);
                    }
                });
                ValueAnimator x = ObjectAnimator.ofFloat(mFocusFrame, "x", newX);
                ValueAnimator y = ObjectAnimator.ofFloat(mFocusFrame, "y", newY);
                mAnim = new AnimatorSet();
                mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                mAnim.playTogether(lp, x, y);
                mAnim.setDuration(300);
                mAnim.start();
            }
        }
    }

    private TypeEvaluator<LayoutParams> mEvaluator = new TypeEvaluator<LayoutParams>() {
        @Override
        public FrameLayout.LayoutParams evaluate(float fraction, FrameLayout.LayoutParams startValue, FrameLayout.LayoutParams endValue) {
            int w = (int) (startValue.width + (endValue.width - startValue.width) * fraction);
            int h = (int) (startValue.height + (endValue.height - startValue.height) * fraction);
            return new FrameLayout.LayoutParams(w, h);
        }
    };

    public void onScrolling() {
        mFocusFrame.setVisibility(INVISIBLE);
    }

    public void fixScrollOffset() {
        if (checkFocusInstance(mNewFocus)) {
            int[] newLoc = new int[2];
            mNewFocus.getLocationOnScreen(newLoc);
            mFocusFrame.setX((newLoc[0] - mNewFocus.getWidth() * (mFocusFrame.getScaleX() - 1) / 2) - getPaddingStart());
            mFocusFrame.setY((newLoc[1] - mNewFocus.getHeight() * (mFocusFrame.getScaleY() - 1) / 2) - getPaddingTop());
            if (mFocusFrame.getVisibility() != VISIBLE) {
                mFocusFrame.setVisibility(VISIBLE);
            }
        }
    }

    private void cancelAnim() {
        if (mAnim != null && mAnim.isRunning()) {
            mAnim.cancel();
        }
    }

    public void setFocusFrame(@DrawableRes int drawable) {
        mFocusFrame.setBackgroundResource(drawable);
    }

    public void setAnimEnable(boolean isAnimEnable) {
        this.isAnimEnable = isAnimEnable;
    }

    public void setFocusViewScale(float scale) {
        this.mScale = scale;
    }

    protected abstract boolean checkFocusInstance(View view);

    protected abstract Drawable getFocusFrame();
}
