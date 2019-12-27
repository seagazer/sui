package com.seagazer.ui.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Default round rect focus frame
 */
public class FocusRoundRectDrawable implements FocusLampDrawable {

    private Paint mPaint;
    private float mRadius;
    private RectF mRectF;

    /**
     * @param strokeWidth The radius of rect
     * @param width       The width of the paint to draw
     * @param color       The color of the paint to draw
     */
    public FocusRoundRectDrawable(float strokeWidth, float width, int color) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(width);
        mPaint.setColor(color);
        mRadius = strokeWidth + 5;// set padding 5px
        mRectF = new RectF();
    }

    @Override
    public void drawFocusFrame(Canvas canvas, Rect focusRect) {
        mRectF.set(focusRect);
        canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
    }
}
