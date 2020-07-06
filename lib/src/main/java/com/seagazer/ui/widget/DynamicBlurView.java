package com.seagazer.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

import com.seagazer.ui.R;
import com.seagazer.ui.util.Logger;

/**
 * 实时动态模糊
 * 可以通过调用{@link #setTargetView(View)} 设置动态模糊目标
 * 调用{@link #invalidate()} 刷新模糊状态
 */
public class DynamicBlurView extends View {
    private static final float SCALE = 4;
    private static final int DEFAULT_OVERLAY = -1;
    private static final int DEFAULT_RADIUS = 15;
    private Bitmap mDrawBitmap;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mScriptIntrinsicBlur;
    private Allocation mAllocationIn, mAllocationOut;
    private View mTarget;
    private Canvas mTempCanvas;
    private Bitmap mTempBitmap;
    private float mBlurRadius;
    private int mOverlayColor;

    public DynamicBlurView(Context context) {
        this(context, null);
    }

    public DynamicBlurView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicBlurView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DynamicBlurView);
        mOverlayColor = ta.getColor(R.styleable.DynamicBlurView_overlayColor, -DEFAULT_OVERLAY);
        mBlurRadius = ta.getInt(R.styleable.DynamicBlurView_blurRadius, DEFAULT_RADIUS);
        ta.recycle();
        initRender();
    }

    private void initRender() {
        mRenderScript = RenderScript.create(getContext());
        mScriptIntrinsicBlur = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
        mScriptIntrinsicBlur.setRadius(mBlurRadius);
    }

    private boolean prepare() {
        if (mRenderScript == null || mTempCanvas == null || mDrawBitmap == null) {
            try {
                mTempBitmap = Bitmap.createBitmap((int) (mTarget.getWidth() / SCALE), (int) (mTarget.getHeight() / SCALE), Bitmap.Config.ARGB_8888);
                mTempCanvas = new Canvas(mTempBitmap);
                mTempCanvas.scale(1.0f / SCALE, 1.0f / SCALE);
                mDrawBitmap = Bitmap.createBitmap((int) (mTarget.getWidth() / SCALE), (int) (mTarget.getHeight() / SCALE), Bitmap.Config.ARGB_8888);
                mAllocationIn = Allocation.createFromBitmap(mRenderScript, mTempBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
                mAllocationOut = Allocation.createTyped(mRenderScript, mAllocationIn.getType());
                return true;
            } catch (Exception e) {
                Logger.e(e.getMessage());
                return false;
            }
        }
        return true;
    }

    protected void blur() {
        mAllocationIn.copyFrom(mTempBitmap);
        mScriptIntrinsicBlur.setInput(mAllocationIn);
        mScriptIntrinsicBlur.forEach(mAllocationOut);
        mAllocationOut.copyTo(mDrawBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTarget != null && prepare()) {
            // if set background color ,erase the background color
            if (mTarget.getBackground() != null && mTarget.getBackground() instanceof ColorDrawable) {
                mTempBitmap.eraseColor(((ColorDrawable) mTarget.getBackground()).getColor());
            } else {
                mTempBitmap.eraseColor(Color.TRANSPARENT);
            }
            mTarget.draw(mTempCanvas);
            blur();
            canvas.save();
            canvas.translate(mTarget.getX() - getX(), mTarget.getY() - getY());
            canvas.scale(SCALE, SCALE);
            canvas.drawBitmap(mDrawBitmap, 0, 0, null);
            canvas.restore();
            if (mOverlayColor != -1) {
                canvas.drawColor(mOverlayColor);
            }
        }
    }

    /**
     * 设置模糊目标
     *
     * @param target 模糊的目标view
     */
    public void setTargetView(final View target) {
        this.mTarget = target;
    }

    /**
     * 设置模糊力度
     *
     * @param radius 模糊半径(0,25]
     */
    public void setRadius(@FloatRange(from = 0, to = 25) float radius) {
        this.mBlurRadius = radius;
        mScriptIntrinsicBlur.setRadius(radius);
    }

    /**
     * 设置遮罩层颜色
     *
     * @param color 遮罩层颜色
     */
    public void setOverlayColor(@ColorInt int color) {
        this.mOverlayColor = color;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // release
        if (mRenderScript != null) {
            mRenderScript.destroy();
        }
    }
}
