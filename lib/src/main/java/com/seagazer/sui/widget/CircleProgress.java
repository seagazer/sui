package com.seagazer.sui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.seagazer.sui.R;

import java.util.Locale;

/**
 * 环形进度条
 */
public class CircleProgress extends View {
    private static final int MIN_BOUND = 200;//px
    private int mProgress = 0;
    private RectF mArcRect;
    private int mForegroundColor;
    private int mBackgroundColor;
    private Paint mPaint;
    private Paint mTextPaint;
    private float cx, cy, mRadius;
    private float mBaseLine;

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        mForegroundColor = ta.getColor(R.styleable.CircleProgress_foregroundColor, Color.BLACK);
        mBackgroundColor = ta.getColor(R.styleable.CircleProgress_backgroundColor, Color.GRAY);
        int textColor = ta.getColor(R.styleable.CircleProgress_textColor, Color.BLACK);
        int textSize = ta.getDimensionPixelSize(R.styleable.CircleProgress_textSize, 56);
        int strokeWidth = ta.getDimensionPixelSize(R.styleable.CircleProgress_arcWidth, 8);
        ta.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(textSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MIN_BOUND;
        int height = MIN_BOUND;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(widthMeasureSpec);
        }
        int bound = Math.min(width, height);
        setMeasuredDimension(bound, bound);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float strokeWidth = mPaint.getStrokeWidth();
        float left;
        float top = left = strokeWidth;
        mArcRect = new RectF(left, top, w - strokeWidth, w - strokeWidth);
        cx = cy = w * 1.0f / 2;
        mRadius = w * 1.0f / 2 - mPaint.getStrokeWidth();
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        mBaseLine = cy + distance;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // background
        mPaint.setColor(mBackgroundColor);
        canvas.drawCircle(cx, cy, mRadius, mPaint);
        // foreground
        mPaint.setColor(mForegroundColor);
        canvas.drawArc(mArcRect, -90, 360 * (mProgress * 1.0f / 100), false, mPaint);
        // text
        String text = String.format(Locale.CHINA, "%d%%", mProgress);
        canvas.drawText(text, cx, mBaseLine, mTextPaint);
    }

    /**
     * Set current progress
     *
     * @param progress [1,100]
     */
    public void setProgress(@IntRange(from = 0, to = 100) int progress) {
        mProgress = progress;
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    /**
     * Set text size
     *
     * @param textSize px
     */
    public void setTextSize(int textSize) {
        mTextPaint.setTextSize(textSize);
    }

    /**
     * Set arc and circle width
     *
     * @param width px
     */
    public void setCircleWidth(int width) {
        mPaint.setStrokeWidth(width);
    }

    /**
     * Set current progress color
     *
     * @param color color of current progress
     */
    public void setForegroundColor(int color) {
        mForegroundColor = color;
    }

    /**
     * Set background progress color
     *
     * @param color color of background progress
     */
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    /**
     * Set text color
     *
     * @param color color of text
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }


}
