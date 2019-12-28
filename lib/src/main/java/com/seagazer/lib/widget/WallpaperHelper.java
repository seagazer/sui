package com.seagazer.lib.widget;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Message;

import androidx.activity.ComponentActivity;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;

public class WallpaperHelper implements LifecycleObserver {
    private static final int MSG_REFRESH_IMAGE = 0x0001;
    private Drawable[] mDrawables = new Drawable[2];
    private boolean isColorFilter;
    private int mFilterColor;
    private Handler mHandler;
    private WeakReference<ComponentActivity> mHost;
    private int mTransitionDelay = 500;
    private int mTransitionDuration = 800;
    private boolean isHostAlive;
    private Drawable mDefaultWallpaper;
    private RatioDrawableWrapper.AlignMode mAlignMode = null;

    public void setupActivity(ComponentActivity activity, @Nullable Drawable defaultWallpaper) {
        isHostAlive = true;
        mDefaultWallpaper = defaultWallpaper;
        mHost = new WeakReference<>(activity);
        activity.getLifecycle().addObserver(this);
        initWindow(activity);
        initPaperHandler();
    }

    private void initWindow(ComponentActivity activity) {
        if (mDefaultWallpaper != null) {
            mDrawables[0] = mDefaultWallpaper;
            activity.getWindow().setBackgroundDrawable(mDrawables[0]);
        } else {
            mDrawables[0] = new ColorDrawable(Color.TRANSPARENT);
        }
    }

    @SuppressLint("HandlerLeak")
    private void initPaperHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_REFRESH_IMAGE && isHostAlive) {
                    if (mDrawables[1] != null) {
                        mDrawables[0] = mDrawables[1];
                    }
                    Drawable newDrawable = (Drawable) msg.obj;
                    if (isColorFilter) {
                        newDrawable.setColorFilter(mFilterColor, PorterDuff.Mode.SRC_ATOP);
                    }
                    mDrawables[1] = newDrawable;
                    TransitionDrawable transitionDrawable = new TransitionDrawable(mDrawables);
                    if (mHost.get() != null) {
                        mHost.get().getWindow().setBackgroundDrawable(transitionDrawable);
                        transitionDrawable.startTransition(mTransitionDuration);
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
    }

    /**
     * Set a new wallPaper to change the background
     *
     * @param resource wallPaper
     */
    public void setWallpaper(@DrawableRes int resource) {
        Drawable drawable = mHost.get().getResources().getDrawable(resource);
        setWallpaper(drawable);
    }

    /**
     * Set a new wallPaper to change the background
     *
     * @param bitmap wallPaper
     */
    public void setWallpaper(@NonNull Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(mHost.get().getResources(), bitmap);
        setWallpaper(drawable);
    }

    /**
     * Set a new wallPaper to change the background
     *
     * @param drawable wallPaper
     */
    public void setWallpaper(@NonNull Drawable drawable) {
        if (isHostAlive()) {
            mHandler.removeMessages(MSG_REFRESH_IMAGE);
            RatioDrawableWrapper drawableWrapper = new RatioDrawableWrapper(drawable, mAlignMode);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_REFRESH_IMAGE, drawableWrapper), mTransitionDelay);
        }
    }

    /**
     * Set a color mask layer overlay the wallpaper
     *
     * @param color the color of the mask layer
     */
    public void addColorFilter(int color) {
        if (isHostAlive()) {
            isColorFilter = true;
            mFilterColor = color;
        }
    }

    /**
     * Set the transition delay so that it will drop the transition when change wallPaper so fast
     *
     * @param delay duration
     */
    public void setTransitionDelay(int delay) {
        if (isHostAlive()) {
            mTransitionDelay = delay;
        }
    }

    /**
     * Set the transition animation duration
     *
     * @param duration duration
     */
    public void setTransitionDuration(int duration) {
        if (isHostAlive()) {
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
        mAlignMode = alignMode;
    }

    // check current activity is activated
    private boolean isHostAlive() {
        return isHostAlive && mHost.get() != null;
    }

}
