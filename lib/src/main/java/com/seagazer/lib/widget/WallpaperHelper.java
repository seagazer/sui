package com.seagazer.lib.widget;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;

/**
 * A helper class to change the background drawable of a wallpaper.
 * <p>
 * Call {@link #attach(ComponentActivity, Drawable)} to bind a target activity and a default display drawable.
 * Call {@link #attach(View, Drawable)}  to bind a target viewGroup and a default display drawable.
 * Call {@link #setTransitionDuration(int)} to set the length of drawable transition.
 * Call {@link #setTransitionDelay(int)} to set the delay time of drawable transition.
 * Call {@link #setColorMask(int)} to set a color mask layer overlay the wallpaper.
 * Call {@link #setWallpaper(int)} or {@link #setWallpaper(Bitmap)} or {@link #setWallpaper(Drawable)}
 * to change a wallpaper.
 * Call {@link #setAlignMode(RatioDrawableWrapper.AlignMode)} to set the display mode if the drawable
 * can not fill the vision.
 */
public class WallpaperHelper implements LifecycleObserver {
    private static final int MSG_REFRESH_IMAGE = 0x0001;
    private CrossFadeDrawable mDrawable;
    private WeakReference<ComponentActivity> mHost;
    private WeakReference<View> mViewHost;
    private boolean isHostAlive = false;
    private boolean isViewAlive = false;
    private int mTransitionDelay = 500;
    private int mTransitionDuration = 500;
    private boolean hasColorMask;
    private int mMaskColor;
    private Handler mHandler;
    private RatioDrawableWrapper.AlignMode mAlignMode = null;
    private boolean isCancel;

    /**
     * Bind a target activity and set a default display drawable
     *
     * @param activity         The target activity which host the vision
     * @param defaultWallpaper The default drawable to display, maybe null
     */
    public void attach(ComponentActivity activity, @Nullable Drawable defaultWallpaper) {
        isHostAlive = true;
        mHost = new WeakReference<>(activity);
        activity.getLifecycle().addObserver(this);
        mDrawable = new CrossFadeDrawable();
        activity.getWindow().setBackgroundDrawable(mDrawable);
        // prepare the vision
        if (defaultWallpaper != null) {
            mDrawable.fadeChange(defaultWallpaper, mTransitionDuration);
        }
        // prepare a handler to handle the message of drawable changed
        prepareHandler();
    }

    /**
     * Bind a target view and set a default display drawable
     *
     * @param view             The target view who to display the drawable
     * @param defaultWallpaper The default drawable to display, maybe null
     */
    public void attach(View view, @Nullable Drawable defaultWallpaper) {
        isViewAlive = true;
        mViewHost = new WeakReference<>(view);
        mDrawable = new CrossFadeDrawable();
        view.setBackground(mDrawable);
        // prepare the vision
        if (defaultWallpaper != null) {
            mDrawable.fadeChange(defaultWallpaper, mTransitionDuration);
        }
        // prepare a handler to handle the message of drawable changed
        prepareHandler();
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                isViewAlive = true;
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                isViewAlive = false;
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (mDrawable != null) {
                    mDrawable.release();
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void prepareHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_REFRESH_IMAGE) {
                    if (isCancel) {
                        return;
                    }
                    Drawable newDrawable = (Drawable) msg.obj;
                    if (isHostAlive()) {
                        mDrawable.fadeChange(newDrawable, mTransitionDuration);
                    }
                    if (isViewAlive()) {
                        mDrawable.fadeChange(newDrawable, mTransitionDuration);
                    }
                }
            }
        };
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        isHostAlive = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onStop() {
        isHostAlive = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mHost != null && mHost.get() != null) {
            mHost.get().getLifecycle().removeObserver(this);
            mHost.clear();
        }
        if (mDrawable != null) {
            mDrawable.release();
        }
    }

    /**
     * Set a new wallPaper to change
     *
     * @param resource wallPaper
     */
    public void setWallpaper(@DrawableRes int resource) {
        checkInit();
        if (isHostAlive()) {
            Drawable drawable = mHost.get().getResources().getDrawable(resource);
            setWallpaper(drawable);
        } else if (isViewAlive()) {
            Drawable drawable = mViewHost.get().getResources().getDrawable(resource);
            setWallpaper(drawable);
        }
    }

    /**
     * Set a new wallPaper to change
     *
     * @param bitmap wallPaper
     */
    public void setWallpaper(@NonNull Bitmap bitmap) {
        checkInit();
        if (isHostAlive()) {
            BitmapDrawable drawable = new BitmapDrawable(mHost.get().getResources(), bitmap);
            setWallpaper(drawable);
        } else if (isViewAlive()) {
            BitmapDrawable drawable = new BitmapDrawable(mViewHost.get().getResources(), bitmap);
            setWallpaper(drawable);
        }
    }

    /**
     * Set a new wallPaper to change
     *
     * @param drawable wallPaper
     */
    public void setWallpaper(@NonNull Drawable drawable) {
        checkInit();
        isCancel = false;
        mHandler.removeMessages(MSG_REFRESH_IMAGE);
        if (isHostAlive() || isViewAlive()) {
            RatioDrawableWrapper drawableWrapper = new RatioDrawableWrapper(drawable, mAlignMode);
            if (hasColorMask) {
                drawableWrapper.setColorMask(mMaskColor);
            }
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_REFRESH_IMAGE, drawableWrapper), mTransitionDelay);
        }
    }

    /**
     * Cancel the prepare wallPaper to set
     */
    public void cancel() {
        isCancel = true;
    }

    /**
     * Set a color mask layer overlay the wallpaper
     *
     * @param color the color of the mask layer
     */
    public void setColorMask(int color) {
        checkInit();
        if (isHostAlive() || isViewAlive()) {
            hasColorMask = true;
            mMaskColor = color;
        }
    }

    /**
     * Set the transition delay so that it will drop the transition when change wallPaper so fast
     *
     * @param delay duration
     */
    public void setTransitionDelay(int delay) {
        checkInit();
        if (mTransitionDelay < mTransitionDuration) {
            Log.w("WallpaperHelper",
                    "[WARNING]: You should set the transitionDelay more than transitionDuration better !");
        }
        if (isHostAlive() || isViewAlive()) {
            mTransitionDelay = delay;
        }
    }

    /**
     * Set the transition animation duration
     *
     * @param duration duration
     */
    public void setTransitionDuration(int duration) {
        checkInit();
        if (mTransitionDelay < mTransitionDuration) {
            Log.w("WallpaperHelper",
                    "[WARNING]: You should set the transitionDelay more than transitionDuration better !");
        }
        if (isHostAlive() || isViewAlive()) {
            mTransitionDuration = duration;
        }
    }

    /**
     * Set the base line of the drawable to clip.
     * The wallPaper drawable always fill width to adjust the background.
     *
     * @param alignMode the base line start to clip, default is align top
     */
    public void setAlignMode(RatioDrawableWrapper.AlignMode alignMode) {
        checkInit();
        mAlignMode = alignMode;
    }

    // check current activity is activated
    private boolean isHostAlive() {
        return isHostAlive && mHost != null && mHost.get() != null;
    }

    // check current view is activated
    private boolean isViewAlive() {
        return isViewAlive && mViewHost != null && mViewHost.get() != null;
    }

    private void checkInit() {
        if (isHostAlive() && isViewAlive()) {
            throw new RuntimeException("A WallpaperHelp instance can only have one host," +
                    " you must call one of attach or attach !");
        }
    }

}
