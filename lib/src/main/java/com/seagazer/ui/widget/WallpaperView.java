package com.seagazer.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.seagazer.ui.R;
import com.seagazer.ui.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供切换背景功能的背景墙
 * 可以通过调用{@link #setBgImage}来改变背景，
 * 通过调用{@link #startRipple}启动随机波纹效果
 */
public class WallpaperView extends FrameLayout {
    private static final int MSG_REFRESH_IMAGE = 0x0001;
    private static final int MSG_REFRESH_RIPPLE = 0x0002;
    private static final int MAX_RIPPLE_COUNT = 10;
    private static final int RIPPLE_REFRESH_TIME = 30;
    private int mTransitionDuration;
    private int mTransitionDelay;
    private Drawable[] mDrawables = new Drawable[2];
    private boolean isColorFilter;
    private int mFilterColor;
    private float mSizeRatio;
    private List<Ripple> mRipples = new ArrayList<>();
    private boolean isRipple;
    private HandlerThread mThread;
    private Handler mHandler;

    public WallpaperView(Context context) {
        this(context, null);
    }

    public WallpaperView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WallpaperView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WallpaperView);
        Drawable drawable = ta.getDrawable(R.styleable.WallpaperView_defaultDrawable);
        mTransitionDuration = ta.getInt(R.styleable.WallpaperView_animDuration, Constants.ANIM_LONG_DURATION);
        mTransitionDelay = ta.getInt(R.styleable.WallpaperView_animDelay, Constants.ANIM_DEFAULT_DELAY);
        mFilterColor = ta.getColor(R.styleable.WallpaperView_filterColor, getResources().getColor(R.color.colorDimDark));
        isColorFilter = ta.getBoolean(R.styleable.WallpaperView_isColorFilter, false);
        ta.recycle();
        if (drawable != null) {
            mDrawables[0] = drawable;
        } else {
            mDrawables[0] = new ColorDrawable(getResources().getColor(R.color.brown_900));
        }
        setBackground(mDrawables[0]);
        setWillNotDraw(false);

        mThread = new HandlerThread(this.getClass().getSimpleName());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSizeRatio = w * 1.0f / h;
    }

    /**
     * 更换背景
     *
     * @param drawable 背景图
     */
    public void setBgImage(Drawable drawable) {
        mHandler.removeMessages(MSG_REFRESH_IMAGE);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_REFRESH_IMAGE, drawable), mTransitionDelay);
    }

    /**
     * 更换背景
     *
     * @param drawable 背景图
     */
    public void setBgImage(Bitmap drawable) {
        mHandler.removeMessages(MSG_REFRESH_IMAGE);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_REFRESH_IMAGE, new BitmapDrawable(getResources(), drawable)), mTransitionDelay);
    }

    /**
     * 添加背景遮罩
     *
     * @param color 遮罩颜色
     */
    public void addColorFilter(int color) {
        isColorFilter = true;
        mFilterColor = color;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isRipple) {
            startRipple();
        }
        mThread.start();
        if (mHandler == null) {
            mHandler = new Handler(mThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == MSG_REFRESH_IMAGE) {
                        if (mDrawables[1] != null) {
                            mDrawables[0] = mDrawables[1];
                        }
                        Drawable newDrawable = (Drawable) msg.obj;
                        Rect original = newDrawable.getBounds();
                        newDrawable.setBounds(original.left, original.top, original.right, (int) ((original.right - original.left) / mSizeRatio + original.top));
                        if (isColorFilter) {
                            newDrawable.setColorFilter(mFilterColor, PorterDuff.Mode.SRC_ATOP);
                        }
                        mDrawables[1] = newDrawable;
                        final TransitionDrawable transitionDrawable = new TransitionDrawable(mDrawables);
                        WallpaperView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                setBackground(transitionDrawable);
                                transitionDrawable.startTransition(mTransitionDuration);
                            }
                        });
                    } else if (msg.what == MSG_REFRESH_RIPPLE) {
                        postInvalidate();
                        mHandler.sendEmptyMessageDelayed(MSG_REFRESH_RIPPLE, RIPPLE_REFRESH_TIME);
                    }
                }
            };
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        mThread.quit();
        if (isRipple) {
            mRipples.clear();
        }
    }

    /**
     * 开启随机波纹效果
     */
    public void startRipple() {
        isRipple = true;
        mHandler.sendEmptyMessage(MSG_REFRESH_RIPPLE);
        createRipples();
    }

    /**
     * 停止随机波纹效果
     */
    public void endRipple() {
        isRipple = false;
        mHandler.removeMessages(MSG_REFRESH_RIPPLE);
        mRipples.clear();
        postInvalidate();
    }

    /**
     * 设置波纹颜色
     *
     * @param color 波纹颜色
     */
    public void setRippleColor(int color) {
        for (Ripple ripple : mRipples) {
            ripple.setRippleColor(color);
        }
    }

    /**
     * 设置波纹线条宽度
     *
     * @param width 线条宽度
     */
    public void setRippleWidth(int width) {
        for (Ripple ripple : mRipples) {
            ripple.setRippleWidth(width);
        }
    }

    /**
     * 设置波纹最大半径
     *
     * @param radius 波纹半径
     */
    public void setRippleRadius(int radius) {
        for (Ripple ripple : mRipples) {
            ripple.setRippleRadius(radius);
        }
    }

    /**
     * 设置波纹涟漪动效时长
     *
     * @param duration 动效时长
     */
    public void setRippleDuration(int duration) {
        for (Ripple ripple : mRipples) {
            ripple.setRippleDuration(duration);
        }
    }

    private void createRipples() {
        post(new Runnable() {
            @Override
            public void run() {
                if (isRipple &&
                        mRipples.size() < MAX_RIPPLE_COUNT) {
                    mRipples.add(new Ripple(getWidth(), getHeight()));
                    // create a ripple pre 1500ms
                    postDelayed(this, 1500);
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isRipple) {
            for (int i = 0; i < mRipples.size(); i++) {
                Ripple ripple = mRipples.get(i);
                ripple.draw(canvas);
            }
        }
    }

    private class Ripple {
        private float maxRadius = 300;
        private int duration = 5000;
        private float x, y;
        private long startTime;
        private int parentWidth;
        private int parentHeight;
        private Paint paint;

        Ripple(int parentWidth, int parentHeight) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(2);
            this.parentWidth = parentWidth;
            this.parentHeight = parentHeight;
            this.x = (float) (Math.random() * parentWidth);
            this.y = (float) (Math.random() * parentHeight);
            startTime = SystemClock.uptimeMillis();
        }

        void setRippleColor(int color) {
            paint.setColor(color);
        }

        void setRippleWidth(int width) {
            paint.setStrokeWidth(width);
        }

        void setRippleDuration(int duration) {
            this.duration = duration;
            startTime = SystemClock.uptimeMillis();
        }

        void setRippleRadius(float maxRadius) {
            this.maxRadius = maxRadius;
            startTime = SystemClock.uptimeMillis();
        }

        void draw(Canvas canvas) {
            long current = SystemClock.uptimeMillis();
            float percent = (current - startTime) * 1.0f / duration;
            if (percent > 1) {
                this.x = (float) (Math.random() * parentWidth);
                this.y = (float) (Math.random() * parentHeight);
                startTime = current;
            } else {
                paint.setAlpha((int) ((percent <= 0.5 ? percent : 1 - percent) * 255));
                canvas.drawCircle(x, y, maxRadius * percent, paint);
            }
        }
    }

}
