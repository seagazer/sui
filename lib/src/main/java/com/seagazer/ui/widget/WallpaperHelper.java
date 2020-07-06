package com.seagazer.ui.widget;

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
 * A helper class to change the background drawable of window or view, with the crossFade animation.
 * <p>
 * Call {@link #attach(ComponentActivity, Drawable)} to setup a target activity and a default display drawable.
 * Call {@link #attach(View, Drawable)}  to setup a target viewGroup and a default display drawable.
 * Call {@link #setCrossFadeDuration(int)} to set the duration of crossFade animation.
 * Call {@link #setCrossFadeDelay(int)} to set the delay time of crossFade animation.
 * Call {@link #setColorMask(int)} to set a color mask layer overlay the wallpaper.
 * Call {@link #setWallpaper(int)} or {@link #setWallpaper(Bitmap)} or {@link #setWallpaper(Drawable)}
 * to change a wallpaper.
 * Call {@link #setAlignMode(AlignMode)} to set the display mode if the drawable
 * can not fill the vision.
 */
public class WallpaperHelper implements LifecycleObserver {
    private static final int MSG_REFRESH_IMAGE = 0x0001;
    private CrossFadeDrawable mDrawable;
    private WeakReference<ComponentActivity> mActivityHost;
    private WeakReference<View> mViewHost;
    private boolean isActivityActivated = false;
    private boolean isViewActivated = false;
    private int mCrossFadeDelay = 500;
    private int mCrossFadeDuration = 500;
    private boolean hasColorMask;
    private int mOverlayMaskColor;
    private Handler mHandler;
    private AlignMode mAlignMode = null;
    private boolean isCancel;

    /**
     * Default construct, then you should call {@link #attach(ComponentActivity, Drawable)} or {@link #attach(View, Drawable)} to attach a host.
     */
    public WallpaperHelper() {

    }

    /**
     * @param host             The window of this host activity will display the drawable
     * @param defaultWallpaper The default drawable to display, maybe null
     */
    public WallpaperHelper(ComponentActivity host, @Nullable Drawable defaultWallpaper) {
        this.attach(host, defaultWallpaper);
    }

    /**
     * @param host             The target view to display the drawable
     * @param defaultWallpaper The default drawable to display, maybe null
     */
    public WallpaperHelper(View host, @Nullable Drawable defaultWallpaper) {
        this.attach(host, defaultWallpaper);
    }

    /**
     * Bind a target activity and set a default display drawable
     *
     * @param activity         The window of this host activity will display the drawable
     * @param defaultWallpaper The default drawable to display, maybe null
     */
    public void attach(ComponentActivity activity, @Nullable Drawable defaultWallpaper) {
        if (isViewActivated) {
            throw new RuntimeException("This wallpaperHelper had attached a view, it must only attach one host!");
        }
        isActivityActivated = true;
        mActivityHost = new WeakReference<>(activity);
        activity.getLifecycle().addObserver(this);
        mDrawable = new CrossFadeDrawable();
        // prepare the default drawable
        activity.getWindow().setBackgroundDrawable(mDrawable);
        if (defaultWallpaper != null) {
            mDrawable.fadeChange(defaultWallpaper, mCrossFadeDuration);
        }
        // prepare a handler to handle the message of drawable changed
        prepareHandler();
    }

    /**
     * Bind a target view and set a default display drawable
     *
     * @param view             The target view to display the drawable
     * @param defaultWallpaper The default drawable to display, maybe null
     */
    public void attach(View view, @Nullable Drawable defaultWallpaper) {
        if (isActivityActivated) {
            throw new RuntimeException("This wallpaperHelper had attached a activity, it must only attach one host!");
        }
        isViewActivated = true;
        mViewHost = new WeakReference<>(view);
        mDrawable = new CrossFadeDrawable();
        // prepare the default drawable
        view.setBackground(mDrawable);
        if (defaultWallpaper != null) {
            mDrawable.fadeChange(defaultWallpaper, mCrossFadeDuration);
        }
        // prepare a handler to handle the message of drawable changed
        prepareHandler();
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                isViewActivated = true;
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                isViewActivated = false;
                release();
            }
        });
    }

    private void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mDrawable != null) {
            mDrawable.release();
        }
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
                    if (isActivityActivated() || isViewActivated()) {
                        mDrawable.fadeChange(newDrawable, mCrossFadeDuration);
                    }
                }
            }
        };
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        isActivityActivated = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onStop() {
        isActivityActivated = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        release();
        if (mActivityHost != null && mActivityHost.get() != null) {
            mActivityHost.get().getLifecycle().removeObserver(this);
            mActivityHost.clear();
        }
    }

    /**
     * Set a new wallPaper to change
     *
     * @param resource wallPaper
     */
    public void setWallpaper(@DrawableRes int resource) {
        checkActivated();
        if (isActivityActivated()) {
            Drawable drawable = mActivityHost.get().getResources().getDrawable(resource);
            setWallpaper(drawable);
        } else if (isViewActivated()) {
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
        checkActivated();
        if (isActivityActivated()) {
            BitmapDrawable drawable = new BitmapDrawable(mActivityHost.get().getResources(), bitmap);
            setWallpaper(drawable);
        } else if (isViewActivated()) {
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
        checkActivated();
        isCancel = false;
        mHandler.removeMessages(MSG_REFRESH_IMAGE);
        if (isActivityActivated() || isViewActivated()) {
            RatioDrawableWrapper drawableWrapper = new RatioDrawableWrapper(drawable, mAlignMode);
            if (hasColorMask) {
                drawableWrapper.setColorMask(mOverlayMaskColor);
            }
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_REFRESH_IMAGE, drawableWrapper), mCrossFadeDelay);
        }
    }

    /**
     * Cancel the prepare wallPaper to set
     */
    public void cancel() {
        isCancel = true;
        mHandler.removeMessages(MSG_REFRESH_IMAGE);
    }

    /**
     * Set a color mask layer overlay the wallpaper
     *
     * @param color the color of the mask layer
     */
    public void setColorMask(int color) {
        hasColorMask = true;
        mOverlayMaskColor = color;
    }

    /**
     * Set the transition delay so that it will drop the transition when change wallPaper so fast
     *
     * @param delay duration
     */
    public void setCrossFadeDelay(int delay) {
        if (mCrossFadeDelay < mCrossFadeDuration) {
            Log.w(this.getClass().getSimpleName(), "[WARNING]: You should set the crossFadeDelay more than crossFadeDuration better!");
        }
        mCrossFadeDelay = delay;
    }

    /**
     * Set the transition animation duration
     *
     * @param duration the duration of transition
     */
    public void setCrossFadeDuration(int duration) {
        if (mCrossFadeDelay < mCrossFadeDuration) {
            Log.w(this.getClass().getSimpleName(), "[WARNING]: You should set the crossFadeDelay more than crossFadeDuration better!");
        }
        mCrossFadeDuration = duration;
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

    /**
     * Check current activity is activated
     *
     * @return true if the activity is activated and resume
     */
    private boolean isActivityActivated() {
        return isActivityActivated && mActivityHost.get() != null;
    }

    /**
     * Check current view is activated
     *
     * @return true if the view is notnull and attach to window
     */
    private boolean isViewActivated() {
        return isViewActivated && mViewHost.get() != null;
    }

    /**
     * Check the helper is attached
     *
     * @return true if the helper is attached and has a host
     */
    private void checkActivated() {
        if (!(isActivityActivated || isViewActivated)) {
            throw new RuntimeException("The wallpaperHelper has no host, you should attach a host first!");
        }
    }

}
