package com.seagazer.ui.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Default round rect focus frame
 */
public class RoundRectFocusDrawable extends FocusDrawable {

    private Paint mPaint;
    private float mRadius;
    private RectF mRectF;

    public RoundRectFocusDrawable(float radius, float width, int color) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(width);
        mPaint.setColor(color);
        mRadius = radius + 5;
        mRectF = new RectF();
    }


    void drawFocusFrame(Canvas canvas, Rect drawRect) {
        mRectF.set(drawRect.left, drawRect.top, drawRect.right, drawRect.bottom);
        canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
    }
}
