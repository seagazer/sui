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
     * @param radius      The radius of rect
     * @param strokeWidth The width of the paint to draw
     * @param color       The color of the paint to draw
     */
    public FocusRoundRectDrawable(float radius, float strokeWidth, int color) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(color);
        mRadius = radius + 5;// set padding 5px
        mRectF = new RectF();
    }

    @Override
    public void drawFocusLamp(Canvas canvas, Rect focusRect) {
        mRectF.set(focusRect);
        canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
    }
}
