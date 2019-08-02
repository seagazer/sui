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
 * A background view to change the bg-image with some special anim.
 * You can call {@link #setBgImage} to change the bg-image, and call {@link #startDance}
 * to playFile the random ripple animation.
 */
public class BackgroundView extends FrameLayout {
    private static final int MSG_REFRESH_IMAGE = 0x0001;
    private static final int MSG_REFRESH_RIPPLE = 0x0002;
    private static final int MAX_RIPPLE_COUNT = 10;
    private static final int RIPPLE_REFRESH_TIME = 30;
    private int mAnimDuration;
    private int mAnimDelay;
    private Drawable[] mDrawables = new Drawable[2];
    private boolean isColorFilter;
    private int mFilterColor;
    private float mSizeRatio;
    private List<Ripple> mRipples = new ArrayList<>();
    private boolean isDancing;
    private HandlerThread mThread;
    private Handler mHandler;

    public BackgroundView(Context context) {
        this(context, null);
    }

    public BackgroundView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BackgroundView);
        Drawable drawable = ta.getDrawable(R.styleable.BackgroundView_defaultDrawable);
        mAnimDuration = ta.getInt(R.styleable.BackgroundView_animDuration, Constants.ANIM_LONG_DURATION);
        mAnimDelay = ta.getInt(R.styleable.BackgroundView_animDelay, Constants.ANIM_DEFAULT_DELAY);
        mFilterColor = ta.getColor(R.styleable.BackgroundView_filterColor, getResources().getColor(R.color.colorDimDark));
        isColorFilter = ta.getBoolean(R.styleable.BackgroundView_isColorFilter, false);
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
     * Change the background image
     *
     * @param drawable The image to changed
     */
    public void setBgImage(Drawable drawable) {
        mHandler.removeMessages(MSG_REFRESH_IMAGE);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_REFRESH_IMAGE, drawable), mAnimDelay);
    }

    /**
     * Change the background image
     *
     * @param drawable The image to changed
     */
    public void setBgImage(Bitmap drawable) {
        mHandler.removeMessages(MSG_REFRESH_IMAGE);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_REFRESH_IMAGE, new BitmapDrawable(getResources(), drawable)), mAnimDelay);
    }

    /**
     * Add a colorFilter of the background image
     *
     * @param color The color of this filter
     */
    public void addColorFilter(int color) {
        isColorFilter = true;
        mFilterColor = color;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isDancing) {
            startDance();
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
                        Drawable drawable = (Drawable) msg.obj;
                        Rect original = drawable.getBounds();
                        drawable.setBounds(original.left, original.top, original.right, (int) ((original.right - original.left) / mSizeRatio + original.top));
                        if (isColorFilter) {
                            drawable.setColorFilter(mFilterColor, PorterDuff.Mode.SRC_ATOP);
                        }
                        mDrawables[1] = drawable;
                        final TransitionDrawable transitionDrawable = new TransitionDrawable(mDrawables);
                        BackgroundView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                setBackground(transitionDrawable);
                                transitionDrawable.startTransition(mAnimDuration);
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
        if (isDancing) {
            mRipples.clear();
        }
    }

    /**
     * Start the random ripple of dancing animation
     */
    public void startDance() {
        isDancing = true;
        mHandler.sendEmptyMessage(MSG_REFRESH_RIPPLE);
        createRipples();
    }

    private void createRipples() {
        post(new Runnable() {
            @Override
            public void run() {
                if (isDancing &&
                        mRipples.size() < MAX_RIPPLE_COUNT) {
                    mRipples.add(new Ripple(getWidth(), getHeight()));
                    // create a ripple pre 1500ms
                    postDelayed(this, 1500);
                }
            }
        });
    }

    /**
     * End the random ripple of dancing animation
     */
    public void endDance() {
        isDancing = false;
        mHandler.removeMessages(MSG_REFRESH_RIPPLE);
        mRipples.clear();
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isDancing) {
            for (int i = 0; i < mRipples.size(); i++) {
                Ripple ripple = mRipples.get(i);
                ripple.draw(canvas);
            }
        }
    }

    private class Ripple {
        private final float maxRadius = 300;
        private final int duration = 5000;
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
