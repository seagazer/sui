package com.seagazer.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A helper to bind a contentView of activity, so the contentView can auto draw focusDrawable when focus changed.
 * <p>
 * Call {@link #setupActivity(ComponentActivity, ViewGroup)} to bind a support activity, so it can auto handle the focus frame changed.
 * Call {@link #addDefaultFocusDrawable(FocusDrawable)} to setup a default focusDrawable.
 * Call {@link #addFocusDrawable(Class, FocusDrawable)} (FocusDrawable)} to add a focusDrawable,
 * when the newFocus instance this Class then will draw this drawable.
 */
@SuppressLint("RestrictedApi")
public class FocusFrameHelper implements ViewTreeObserver.OnGlobalFocusChangeListener, ViewTreeObserver.OnDrawListener,
        ViewTreeObserver.OnGlobalLayoutListener, LifecycleObserver {
    private ComponentActivity mHost;
    private FrameLayout mDecorView;
    private FocusDrawer mFocusDrawer;
    private View mFocused;
    private Rect mCanvasRect;
    private ViewGroup mVisibleArea;
    private boolean isFirstLayout = true;
    private boolean isHostAlive;

    /**
     * Bind a support activity to handle the focus frame changed
     *
     * @param activity    The displaying activity, only support {@link ComponentActivity} because it implements LifecycleOwner
     * @param visibleArea The parent who should hold all focus views, if null the default visibleArea is fullscreen
     */
    public void setupActivity(@NonNull ComponentActivity activity, @Nullable ViewGroup visibleArea) {
        mHost = activity;
        isHostAlive = true;
        mHost.getLifecycle().addObserver(this);
        mFocusDrawer = new FocusDrawer(mHost);
        mDecorView = (FrameLayout) mHost.getWindow().getDecorView();
        if (visibleArea != null) {
            mVisibleArea = visibleArea;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onResume() {
        ViewTreeObserver vto = mDecorView.getViewTreeObserver();
        if (vto.isAlive()) {
            // activity onResume, vto register
            if (isFirstLayout) {
                vto.addOnGlobalLayoutListener(this);
            }
            vto.addOnGlobalFocusChangeListener(FocusFrameHelper.this);
            vto.addOnDrawListener(FocusFrameHelper.this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        ViewTreeObserver vto = mDecorView.getViewTreeObserver();
        if (vto.isAlive()) {
            // activity onPause, vto unregister
            vto.removeOnGlobalFocusChangeListener(FocusFrameHelper.this);
            vto.removeOnDrawListener(FocusFrameHelper.this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        isHostAlive = false;
        // activity onDestroy, release all
        mHost.getLifecycle().removeObserver(this);
        mHost = null;
        mDecorView.removeView(mFocusDrawer);
        mFocusDrawer = null;
        mDecorView = null;
        mFocused = null;
    }

    @Override
    public void onGlobalLayout() {
        isFirstLayout = false;
        if (mVisibleArea != null) {
            mCanvasRect = new Rect();
            int[] location = new int[2];
            mVisibleArea.getLocationOnScreen(location);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // if not clip padding, not calculate the parent's padding
                if (!mVisibleArea.getClipToPadding()) {
                    mCanvasRect.set(location[0], location[1],
                            location[0] + mVisibleArea.getWidth(), location[1] + mVisibleArea.getHeight());
                } else {
                    int left = location[0] + mVisibleArea.getPaddingLeft();
                    int top = location[1] + mVisibleArea.getPaddingTop();
                    mCanvasRect.set(left, top,
                            left + mVisibleArea.getWidth(), top + mVisibleArea.getHeight());
                }
            } else {
                mCanvasRect.set(location[0], location[1],
                        location[0] + mVisibleArea.getWidth(), location[1] + mVisibleArea.getHeight());
            }
        }
        mDecorView.addView(mFocusDrawer, -1, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        ViewTreeObserver vto = mDecorView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeOnGlobalLayoutListener(this);
        }
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (isHostAlive) {
            if (newFocus != null && mFocused != newFocus) {
                mFocused = newFocus;
            }
        }
    }

    @Override
    public void onDraw() {
        if (isHostAlive) {
            if (mFocusDrawer != null && mFocused != null) {
                mFocusDrawer.drawFocusFrame(mFocused);
            }
        }
    }

    /**
     * Set a default drawable to draw focus, when the focus view has no target clazz in the mapping
     *
     * @param drawable A drawable to draw focusHighLight
     */
    public void addDefaultFocusDrawable(FocusDrawable drawable) {
        if (isHostAlive) {
            if (mFocusDrawer != null) {
                mFocusDrawer.addDefaultFocusDrawable(drawable);
            }
        }
    }

    /**
     * Set a drawable to draw focus, when the focus instanceOf this clazz
     *
     * @param clazz    The class instanceOf the focus view
     * @param drawable A drawable to draw focusHighLight
     */
    public void addFocusDrawable(Class<? extends View> clazz, FocusDrawable drawable) {
        if (isHostAlive) {
            if (mFocusDrawer != null) {
                mFocusDrawer.addFocusDrawable(clazz, drawable);
            }
        }
    }

    /**
     * A drawer to draw the focusDrawable when the focus changed
     */
    private class FocusDrawer extends View {
        private Rect mCurrentRect, mLastRect;
        private FocusDrawable mCurDrawable;
        private FocusDrawable mDefaultDrawable;
        private Map<Class<? extends View>, FocusDrawable> mFocusDrawables = new HashMap<>();
        private boolean isDirty = false;

        FocusDrawer(Context context) {
            super(context);
            mCurrentRect = new Rect();
            mLastRect = new Rect();
        }

        void drawFocusFrame(View view) {
            if (isHostAlive) {
                if (checkFocusDrawableNotNull(view)) {
                    // calculate the focusView of parent
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    int width = view.getWidth();
                    int height = view.getHeight();
                    float scaleX = view.getScaleX();
                    float scaleY = view.getScaleY();
                    int offsetX = (int) (width * (scaleX - 1));
                    int offsetY = (int) (height * (scaleY - 1));
                    int left = location[0];
                    int top = location[1];
                    int right = left + width + offsetX;
                    int bottom = top + height + offsetY;
                    mCurrentRect.set(left, top, right, bottom);
                    if (mLastRect.left == mCurrentRect.left && mLastRect.top == mCurrentRect.top &&
                            mLastRect.right == mCurrentRect.right && mLastRect.bottom == mCurrentRect.bottom) {
                        // end draw, break the draw circulation
                        return;
                    }
                    invalidate();
                    invalidate(new Rect());
                    isDirty = true;
                    mLastRect.set(mCurrentRect);
                }
            }
        }

        private boolean checkFocusDrawableNotNull(View view) {
            mCurDrawable = mDefaultDrawable;
            Set<Map.Entry<Class<? extends View>, FocusDrawable>> entrySet = mFocusDrawables.entrySet();
            for (Map.Entry<Class<? extends View>, FocusDrawable> entry : entrySet) {
                Class<? extends View> key = entry.getKey();
                if (view.getClass() == key) {
                    mCurDrawable = entry.getValue();
                    break;
                }
            }
            // not set the focus drawable, not do draw
            if (mCurDrawable == null) {
                // if is dirty, clear the last focus drawable
                if (isDirty) {
                    invalidate();
                }
                return false;
            }
            return true;
        }

        public void addDefaultFocusDrawable(FocusDrawable focusDrawable) {
            this.mDefaultDrawable = focusDrawable;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (isHostAlive) {
                if (mCanvasRect != null) {
                    canvas.save();
                    canvas.clipRect(mCanvasRect);
                    drawFocus(canvas);
                    canvas.restore();
                } else {
                    drawFocus(canvas);
                }
            }
        }

        private void drawFocus(Canvas canvas) {
            if (isDirty && mCurDrawable == null) {
                canvas.drawColor(Color.TRANSPARENT);
                isDirty = false;
            } else if (mCurDrawable != null) {
                mCurDrawable.drawFocusFrame(canvas, mCurrentRect);
            }
        }

        public void addFocusDrawable(Class<? extends View> clazz, FocusDrawable drawable) {
            mFocusDrawables.put(clazz, drawable);
        }
    }
}
