package com.seagazer.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.seagazer.ui.R;
import com.seagazer.ui.util.Constants;


// Silence
public class LoadingView extends View implements ValueAnimator.AnimatorUpdateListener {
    private Paint mForegroundPaint;
    private Paint mBackgroundPaint;
    private PathMeasure mPathMeasure;
    private ValueAnimator mAnimator;
    private Path mDrawPath;
    private float mProgress;
    private float mHalfPaintStrokeWidth;
    private float mCenterY;
    private int mAnimDuration;
    private int mBackgroundColor, mForegroundColor;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        mAnimDuration = ta.getInt(R.styleable.LoadingView_animDuration, Constants.ANIM_PROGRESS_DEFAULT_DURATION);
        mBackgroundColor = ta.getColor(R.styleable.LoadingView_backgroundColor, Color.DKGRAY);
        mForegroundColor = ta.getColor(R.styleable.LoadingView_foregroundColor, Color.LTGRAY);
        ta.recycle();

        mForegroundPaint = new Paint();
        mForegroundPaint.setAntiAlias(true);
        mForegroundPaint.setStyle(Paint.Style.STROKE);
        mForegroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mForegroundPaint.setColor(mForegroundColor);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackgroundPaint.setColor(mBackgroundColor);

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(mAnimDuration);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addUpdateListener(this);

        mDrawPath = new Path();
        setAlpha(0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mForegroundPaint.setStrokeWidth(h);
        mBackgroundPaint.setStrokeWidth(h);
        mCenterY = mHalfPaintStrokeWidth = h / 2f;
        Path backgroundPath = new Path();
        backgroundPath.moveTo(0, mCenterY);
        backgroundPath.lineTo(w, mCenterY);
        mPathMeasure = new PathMeasure(backgroundPath, false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getAlpha() == 1) {
            mAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mAnimator.isRunning()) {
            mAnimator.cancel();// release animation
        }
        super.onDetachedFromWindow();
    }

    public Animator getAnimator() {
        return mAnimator;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // background
        canvas.drawLine(mHalfPaintStrokeWidth, mCenterY, getWidth() - mHalfPaintStrokeWidth, mCenterY, mBackgroundPaint);
        // foreground - progress
        float length = mPathMeasure.getLength();
        mDrawPath.reset();
        float stop = mProgress * length;
        float start = (float) (stop - (0.5 - Math.abs(mProgress - 0.5)) * length) + mHalfPaintStrokeWidth;
        mPathMeasure.getSegment(start, stop, mDrawPath, true);
        canvas.drawPath(mDrawPath, mForegroundPaint);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (mPathMeasure == null) {
            return;
        }
        mProgress = (float) animation.getAnimatedValue();
        invalidate();
    }

    public void show() {
        if (getAlpha() == 0) {
            animate().alpha(1).setDuration(500).start();
            if (mAnimator.isRunning()) {
                mAnimator.cancel();
            }
            mAnimator.start();
        }
    }

    public void dismiss() {
        if (getAlpha() == 1) {
            animate().alpha(0).setDuration(500).start();
            if (mAnimator.isRunning()) {
                mAnimator.cancel();
            }
        }
    }

    public void setAnimDuration(int duration) {
        mAnimDuration = duration;
    }

    public void setProgressBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public void setProgressForegroundColor(int color) {
        mForegroundColor = color;
    }
}
