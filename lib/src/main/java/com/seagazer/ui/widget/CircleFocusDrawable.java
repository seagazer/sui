package com.seagazer.ui.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Default circle focus frame
 */
public class CircleFocusDrawable extends FocusDrawable {

    private Paint mPaint;

    public CircleFocusDrawable(float width, int color) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(width);
        mPaint.setColor(color);
    }

    @Override
    void drawFocusFrame(Canvas canvas, Rect drawRect) {
        float cx = (drawRect.right - drawRect.left) / 2.0f + drawRect.left;
        float cy = (drawRect.bottom - drawRect.top) / 2.0f + drawRect.top;
        float r = cx - drawRect.left;
        canvas.drawCircle(cx, cy, r, mPaint);
    }
}
