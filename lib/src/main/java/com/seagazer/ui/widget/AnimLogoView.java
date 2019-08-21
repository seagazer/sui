package com.seagazer.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.seagazer.ui.R;
import com.seagazer.ui.util.Constants;
import com.seagazer.ui.util.Logger;

/**
 * 提供开屏logo动画效果
 * 可以通过调用{@link #setLogoName(String)}设置logo名称
 * 通过调用{@link #playAnimation()}开启logo动画
 */
public class AnimLogoView extends View {
    private static final String DEFAULT_LOGO = "SEAGAZER";
    private static final int DEFAULT_TEXT_PADDING = 10;
    private SparseArray<String> mLogoTexts = new SparseArray<>();
    private SparseArray<PointF> mQuietPoints = new SparseArray<>();
    private SparseArray<PointF> mRadonPoints = new SparseArray<>();
    private ValueAnimator mOffsetAnimator;
    private ValueAnimator mGradientAnimator;
    private Paint mPaint;
    private int mTextPadding;
    private int mTextColor;
    private int mTextSize;
    private float mOffsetAnimProgress;
    private int mOffsetDuration;
    private boolean isOffsetAnimEnd;
    private int mGradientDuration;
    private LinearGradient mLinearGradient;
    private int mGradientColor;
    private Matrix mGradientMatrix;
    private int mMatrixTranslate;
    private boolean isAutoPlay;
    private int mWidth, mHeight;

    public AnimLogoView(Context context) {
        this(context, null);
    }

    public AnimLogoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimLogoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AnimLogoView);
        String logoName = ta.getString(R.styleable.AnimLogoView_logoName);
        isAutoPlay = ta.getBoolean(R.styleable.AnimLogoView_autoPlay, true);
        mOffsetDuration = ta.getInt(R.styleable.AnimLogoView_offsetAnimDuration, Constants.ANIM_LOGO_DURATION);
        mGradientDuration = ta.getInt(R.styleable.AnimLogoView_gradientAnimDuration, Constants.ANIM_LOGO_GRADIENT_DURATION);
        mTextColor = ta.getColor(R.styleable.AnimLogoView_textColor, getResources().getColor(R.color.textDefault));
        mGradientColor = ta.getColor(R.styleable.AnimLogoView_gradientColor, getResources().getColor(R.color.yellow_700));
        mTextPadding = ta.getDimensionPixelSize(R.styleable.AnimLogoView_textPadding, DEFAULT_TEXT_PADDING);
        mTextSize = ta.getDimensionPixelSize(R.styleable.AnimLogoView_textSize, getResources().getDimensionPixelOffset(R.dimen.logoTitle));
        ta.recycle();
        if (TextUtils.isEmpty(logoName)) {
            logoName = DEFAULT_LOGO;// default logo
        }
        fillLogoTextArray(logoName);
        initPaint();
        initOffsetAnimation();
    }

    // fill the text to array
    private void fillLogoTextArray(String logoName) {
        if (TextUtils.isEmpty(logoName)) {
            return;
        }
        if (mLogoTexts.size() > 0) {
            mLogoTexts.clear();
        }
        for (int i = 0; i < logoName.length(); i++) {
            char c = logoName.charAt(i);
            String s = String.valueOf(c);
            mLogoTexts.put(i, s);
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
    }

    // init the translation animation
    private void initOffsetAnimation() {
        mOffsetAnimator = ValueAnimator.ofFloat(0, 1);
        mOffsetAnimator.setDuration(mOffsetDuration);
        mOffsetAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mQuietPoints.size() <= 0 || mRadonPoints.size() <= 0) {
                    return;
                }
                mOffsetAnimProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mOffsetAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mGradientAnimator != null) {
                    isOffsetAnimEnd = true;
                    mPaint.setShader(mLinearGradient);
                    mGradientAnimator.start();
                }
            }
        });
    }

    // init the gradient animation
    private void initGradientAnimation(int width) {
        mGradientAnimator = ValueAnimator.ofInt(0, 2 * width);
        mGradientAnimator.setDuration(mGradientDuration);
        mGradientAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMatrixTranslate = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        mLinearGradient = new LinearGradient(-width, 0, 0, 0, new int[]{mTextColor, mGradientColor, mTextColor},
                new float[]{0, 0.5f, 1}, Shader.TileMode.CLAMP);
        mGradientMatrix = new Matrix();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == VISIBLE && isAutoPlay) {
            mOffsetAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // release animation
        if (mOffsetAnimator != null && mOffsetAnimator.isRunning()) {
            mOffsetAnimator.cancel();
        }
        if (mGradientAnimator != null && mGradientAnimator.isRunning()) {
            mGradientAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }

    public Animator getAnimator() {
        return mOffsetAnimator;
    }

    /**
     * 开启动画
     */
    public void playAnimation() {
        if (getVisibility() == VISIBLE) {
            if (mOffsetAnimator.isRunning()) {
                mOffsetAnimator.cancel();
            }
            mOffsetAnimator.start();
        } else {
            Logger.w("The view is not visible, not to playFile the animation .");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initLogoCoordinate();
        initGradientAnimation(w);
    }

    private void initLogoCoordinate() {
        float centerY = mHeight / 2f + mPaint.getTextSize() / 2;
        // calculate the final xy of the text
        float totalLength = 0;
        for (int i = 0; i < mLogoTexts.size(); i++) {
            String str = mLogoTexts.get(i);
            float currentLength = mPaint.measureText(str);
            if (i != mLogoTexts.size() - 1) {
                totalLength += currentLength + mTextPadding;
            } else {
                totalLength += currentLength;
            }
        }
        // the draw width of the logo must small than the width of this AnimLogoView
        if (totalLength > mWidth) {
            throw new IllegalStateException("The text of logoName is too large that this view can not display all text");
        }
        float startX = (mWidth - totalLength) / 2;
        if (mQuietPoints.size() > 0) {
            mQuietPoints.clear();
        }
        for (int i = 0; i < mLogoTexts.size(); i++) {
            String str = mLogoTexts.get(i);
            float currentLength = mPaint.measureText(str);
            mQuietPoints.put(i, new PointF(startX, centerY));
            startX += currentLength + mTextPadding;
        }
        // generate random start xy of the text
        if (mRadonPoints.size() > 0) {
            mRadonPoints.clear();
        }
        for (int i = 0; i < mLogoTexts.size(); i++) {
            mRadonPoints.put(i, new PointF((float) Math.random() * mWidth, (float) Math.random() * mHeight));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isOffsetAnimEnd) {// offset animation
            mPaint.setAlpha((int) Math.min(255, 255 * mOffsetAnimProgress + 100));
            for (int i = 0; i < mQuietPoints.size(); i++) {
                PointF quietP = mQuietPoints.get(i);
                PointF radonP = mRadonPoints.get(i);
                float x = radonP.x + (quietP.x - radonP.x) * mOffsetAnimProgress;
                float y = radonP.y + (quietP.y - radonP.y) * mOffsetAnimProgress;
                canvas.drawText(mLogoTexts.get(i), x, y, mPaint);
            }
        } else {// gradient animation
            for (int i = 0; i < mQuietPoints.size(); i++) {
                PointF quietP = mQuietPoints.get(i);
                canvas.drawText(mLogoTexts.get(i), quietP.x, quietP.y, mPaint);
            }
            mGradientMatrix.setTranslate(mMatrixTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
        }
    }

    /**
     * 设置logo名
     *
     * @param logoName logo名称
     */
    public void setLogoName(String logoName) {
        fillLogoTextArray(logoName);
        // if set the new logoName, should refresh the coordinate again
        initLogoCoordinate();
    }

    public void setOffsetAnimDuration(int duration) {
        mOffsetDuration = duration;
    }

    public void setGradientAnimDuration(int duration) {
        mGradientDuration = duration;
    }

    /**
     * 设置logo字体边距
     *
     * @param padding 字体边距
     */
    public void setTextPadding(int padding) {
        mTextPadding = padding;
    }

    /**
     * 设置logo字体颜色
     *
     * @param color 字体颜色
     */
    public void setTextColor(int color) {
        mTextColor = color;
    }

    /**
     * 设置logo字体大小
     *
     * @param size 字体大小
     */
    public void setTextSize(int size) {
        mTextSize = size;
    }
}
