package com.seagazer.sui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.seagazer.sui.R;
import com.seagazer.sui.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A container can change the background drawable of a wallpaper.
 * <p>
 * Call {@link #setTransitionDuration(int)} to set the length of drawable transition.
 * Call {@link #setTransitionDelay(int)}  to set the delay time of drawable transition.
 * Call {@link #setWallpaper(int)} or {@link #setWallpaper(Bitmap)} or {@link #setWallpaper(Drawable)}
 * to change a wallpaper.
 * Call {@link #setAlignMode(AlignMode)} to set the display mode if the drawable
 * can not fill the vision.
 */
public class WallpaperView extends FrameLayout {
    private static final int MSG_REFRESH_IMAGE = 0x0001;
    private static final int MSG_REFRESH_RIPPLE = 0x0002;
    private static final int ALIGN_MODE_START = 0;
    private static final int ALIGN_MODE_CENTER = 1;
    private static final int ALIGN_MODE_END = 2;
    private static final int MAX_RIPPLE_COUNT = 10;
    private static final int RIPPLE_REFRESH_TIME = 30;
    private CrossFadeDrawable mDrawable;
    private int mTransitionDuration;
    private int mTransitionDelay;
    private boolean hasColorMask;
    private int mFilterColor;
    private List<Ripple> mRipples = new ArrayList<>();
    private boolean isRipple;
    private HandlerThread mThread;
    private Handler mHandler;
    private AlignMode mAlignMode;

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
        hasColorMask = ta.getBoolean(R.styleable.WallpaperView_isColorMask, false);
        int alignMode = ta.getInt(R.styleable.WallpaperView_alignMode, 0);
        ta.recycle();
        mDrawable = new CrossFadeDrawable();
        setBackground(mDrawable);
        if (drawable != null) {
            mDrawable.fadeChange(drawable, mTransitionDuration);
        }
        if (alignMode == ALIGN_MODE_START) {
            mAlignMode = AlignMode.START;
        } else if (alignMode == ALIGN_MODE_CENTER) {
            mAlignMode = AlignMode.CENTER;
        } else if (alignMode == ALIGN_MODE_END) {
            mAlignMode = AlignMode.END;
        }
        setWillNotDraw(false);

        mThread = new HandlerThread(this.getClass().getSimpleName());
    }

    /**
     * Set a new wallPaper to change
     *
     * @param resource wallPaper
     */
    public void setWallpaper(@DrawableRes int resource) {
        Drawable drawable = getResources().getDrawable(resource);
        setWallpaper(drawable);
    }

    /**
     * Set a new wallPaper to change
     *
     * @param bitmap wallPaper
     */
    public void setWallpaper(@NonNull Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        setWallpaper(drawable);
    }

    /**
     * Set a new wallPaper to change
     *
     * @param drawable wallPaper
     */
    public void setWallpaper(@NonNull Drawable drawable) {
        mHandler.removeMessages(MSG_REFRESH_IMAGE);
        RatioDrawableWrapper drawableWrapper = new RatioDrawableWrapper(drawable, mAlignMode);
        if (hasColorMask) {
            drawableWrapper.setColorMask(mFilterColor);
        }
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_REFRESH_IMAGE, drawableWrapper), mTransitionDelay);
    }

    /**
     * Set a color mask layer overlay the wallpaper
     *
     * @param color the color of the mask layer
     */
    public void setColorMask(int color) {
        hasColorMask = true;
        mFilterColor = color;
    }

    /**
     * Set the transition delay so that it will drop the transition when change wallPaper so fast
     *
     * @param delay duration
     */
    public void setTransitionDelay(int delay) {
        mTransitionDelay = delay;
    }

    /**
     * Set the transition animation duration
     *
     * @param duration duration
     */
    public void setTransitionDuration(int duration) {
        mTransitionDuration = duration;
    }

    /**
     * Set the base line of the drawable to clip.
     * The wallPaper drawable always fill width to adjust the background.
     *
     * @param alignMode the base line start to clip, default is align top
     */
    public void setAlignMode(AlignMode alignMode) {
        mAlignMode = alignMode;
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
                        Drawable newDrawable = (Drawable) msg.obj;
                        WallpaperView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                mDrawable.fadeChange(newDrawable, mTransitionDuration);
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
        if (mDrawable != null) {
            mDrawable.release();
        }
    }

    /**
     * Start a random ripple anim
     */
    public void startRipple() {
        isRipple = true;
        mHandler.sendEmptyMessage(MSG_REFRESH_RIPPLE);
        createRipples();
    }

    /**
     * Stop the random ripple anim
     */
    public void endRipple() {
        isRipple = false;
        mHandler.removeMessages(MSG_REFRESH_RIPPLE);
        mRipples.clear();
        postInvalidate();
    }

    /**
     * Set the color of ripper
     *
     * @param color the color of ripper
     */
    public void setRippleColor(int color) {
        for (Ripple ripple : mRipples) {
            ripple.setRippleColor(color);
        }
    }

    /**
     * Set the width of ripper
     *
     * @param width the width of ripper
     */
    public void setRippleWidth(int width) {
        for (Ripple ripple : mRipples) {
            ripple.setRippleWidth(width);
        }
    }

    /**
     * Set the radius of ripper
     *
     * @param radius the radius of ripper
     */
    public void setRippleRadius(int radius) {
        for (Ripple ripple : mRipples) {
            ripple.setRippleRadius(radius);
        }
    }

    /**
     * Set the anim duration of ripper
     *
     * @param duration the length of duration
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
