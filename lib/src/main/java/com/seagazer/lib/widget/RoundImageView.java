package com.seagazer.lib.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class RoundImageView extends AppCompatImageView {
    private final Paint mPaint = new Paint();
    private Bitmap mBitmap;
    private RectF rectF;
    private Path mRoundPath;
    private float mRadius = 30;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF = new RectF(0, 0, w, h);
        init();
        // TODO: 2019/9/3   setter the corner every one
        mRoundPath = new Path();
        float[] a = {20, 20, 0, 0, 20, 20, 0, 0};
        mRoundPath.addRoundRect(rectF, a, Path.Direction.CCW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null || rectF == null) {
            return;
        }
        canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);
//        canvas.drawPath(mRoundPath, mPaint);
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initBitmap() {
        mBitmap = getBitmapFromDrawable(getDrawable());
        init();
    }

    private void init() {
        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }
        if (mBitmap == null) {
            return;
        }
        BitmapShader bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);
        invalidate();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initBitmap();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initBitmap();
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }
}
