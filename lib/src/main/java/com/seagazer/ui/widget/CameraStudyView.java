package com.seagazer.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.seagazer.ui.util.Logger;

public class CameraStudyView extends View {
    private Bitmap bitmap;
    private Camera camera;
    private Matrix matrix;
    private Rect outRect;
    private int drawableRes;

    private Paint paint = new Paint();

    {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(Color.BLUE);
    }

    public CameraStudyView(Context context) {
        this(context, null);
    }

    public CameraStudyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        camera = new Camera();
        matrix = new Matrix();
        outRect = new Rect();
    }

    public void translate(float x, float y, float z) {
        matrix.reset();
        camera.save();
        camera.translate(x, y, z);
        camera.getMatrix(matrix);
        camera.restore();
        invalidate();
    }

    public void rotate(float x, float y, float z) {
        matrix.reset();
        camera.save();
        camera.rotate(x, y, z);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-getWidth() / 2f, -getHeight() / 2f);
        matrix.postTranslate(getWidth() / 2f, getHeight() / 2f);
        invalidate();
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawable = getResources().getDrawable(drawableRes);
        Rect rect = new Rect();
        getDrawingRect(rect);
        drawable.setBounds(rect);
        outRect.set(0, 0, w, h);
        drawable.draw(canvas);
        Logger.d(this.outRect.toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, matrix, null);
            canvas.drawRect(outRect, paint);
        }
    }
}
