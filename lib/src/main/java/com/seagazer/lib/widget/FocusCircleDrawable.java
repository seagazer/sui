package com.seagazer.lib.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Default circle focus frame
 */
public class FocusCircleDrawable implements FocusLampDrawable {

    private Paint mPaint;

    /**
     * @param strokeWidth The width of the paint to draw
     * @param color       The color of the paint to draw
     */
    public FocusCircleDrawable(float strokeWidth, int color) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(color);
    }

    @Override
    public void drawFocusLamp(Canvas canvas, Rect focusRect) {
        int width = focusRect.right - focusRect.left;
        int height = focusRect.bottom - focusRect.top;
        if (width != height) {
            throw new RuntimeException("The CircleFocusDrawable only support square view, check the width and height of the focusView.");
        }
        float cx = width / 2.0f + focusRect.left;
        float cy = height / 2.0f + focusRect.top;
        float r = cx - focusRect.left;
        canvas.drawCircle(cx, cy, r, mPaint);
    }
}
