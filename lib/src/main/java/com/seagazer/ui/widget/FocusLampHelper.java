package com.seagazer.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
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
 * A helper framework to bind the displaying activity, so that it can auto draw focusDrawable when focus changed.
 * If you use frameLayout as the rootView, you can also use {@link FocusLampContainer} instead.
 * <p>
 * Call {@link #setupActivity(ComponentActivity, ViewGroup)} to bind a support activity, so it can auto handle the focus frame changed.
 * <p>
 * Call {@link #addDefaultFocusDrawable(FocusLampDrawable)} to setup a default focusDrawable.
 * <p>
 * Call {@link #addFocusDrawable(Class, FocusLampDrawable)} to add a focusDrawable and bind a class, when the newFocus as same as this Class,
 * it will draw this focusDrawable, not the default focusDrawable.
 */
@SuppressLint("RestrictedApi")
public class FocusLampHelper implements ViewTreeObserver.OnGlobalFocusChangeListener, ViewTreeObserver.OnDrawListener,
        ViewTreeObserver.OnGlobalLayoutListener, LifecycleObserver {
    private ComponentActivity mHost;
    private FrameLayout mDecorView;
    private FocusDrawer mFocusDrawer;
    private View mFocused;
    private Rect mCanvasRect;
    private ViewGroup mDrawArea;
    private boolean isFirstLayout = true;
    private boolean isHostAlive;

    /**
     * Bind a support activity to handle the focus frame changed.
     *
     * @param activity The displaying activity, only support {@link ComponentActivity} because it implements LifecycleOwner.
     * @param drawArea Limit the drawing area, if null the default visibleArea is fullscreen.
     *                 For example, you set a viewGroup here, if the newFocus is out of the drawRect of this viewGroup,
     *                 the framework will not draw the focusDrawable.
     */
    public void setupActivity(@NonNull ComponentActivity activity, @Nullable ViewGroup drawArea) {
        mHost = activity;
        isHostAlive = true;
        mHost.getLifecycle().addObserver(this);
        mFocusDrawer = new FocusDrawer(mHost);
        mDecorView = (FrameLayout) mHost.getWindow().getDecorView();
        if (drawArea != null) {
            mDrawArea = drawArea;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onResume() {
        ViewTreeObserver vto = mDecorView.getViewTreeObserver();
        if (vto.isAlive()) {
            // Activity onResume, vto register
            if (isFirstLayout) {
                vto.addOnGlobalLayoutListener(this);
            }
            vto.addOnGlobalFocusChangeListener(FocusLampHelper.this);
            vto.addOnDrawListener(FocusLampHelper.this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        ViewTreeObserver vto = mDecorView.getViewTreeObserver();
        if (vto.isAlive()) {
            // Activity onPause, vto unregister
            vto.removeOnGlobalFocusChangeListener(FocusLampHelper.this);
            vto.removeOnDrawListener(FocusLampHelper.this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        isHostAlive = false;
        // Activity onDestroy, release all
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
        checkDrawArea();
        mDecorView.addView(mFocusDrawer, -1, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        ViewTreeObserver vto = mDecorView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeOnGlobalLayoutListener(this);
        }
    }

    // Check if set the drawArea can draw focusDrawable
    private void checkDrawArea() {
        if (mDrawArea != null) {
            mCanvasRect = new Rect();
            int[] location = new int[2];
            mDrawArea.getLocationOnScreen(location);
            int left = location[0] + mDrawArea.getPaddingLeft();
            int top = location[1] + mDrawArea.getPaddingTop();
            mCanvasRect.set(left, top,
                    left + mDrawArea.getWidth(), top + mDrawArea.getHeight());
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
    public void addDefaultFocusDrawable(FocusLampDrawable drawable) {
        if (isHostAlive) {
            if (mFocusDrawer != null) {
                mFocusDrawer.addDefaultFocusDrawable(drawable);
            }
        }
    }

    /**
     * Set a drawable to draw focus, when the focus instanceOf this clazz
     *
     * @param clazz    The class of the focus view
     * @param drawable A drawable to draw focusHighLight
     */
    public void addFocusDrawable(Class<? extends View> clazz, FocusLampDrawable drawable) {
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
        private FocusLampDrawable mCurDrawable;
        private FocusLampDrawable mDefaultDrawable;
        private Map<Class<? extends View>, FocusLampDrawable> mFocusDrawables = new HashMap<>();
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
                        // End draw, break the draw circulation
                        return;
                    }
                    // Current focus is out of canvas area and isDirty, clear focus drawable
                    if (!isFocusInCanvas() && isDirty) {
                        mCurDrawable = null;
                        invalidate();
                        return;
                    }
                    // Current focus is in canvas area, draw focus drawable
                    invalidate();
                    isDirty = true;
                    mLastRect.set(mCurrentRect);
                }
            }
        }

        private boolean checkFocusDrawableNotNull(View view) {
            mCurDrawable = mDefaultDrawable;
            Set<Map.Entry<Class<? extends View>, FocusLampDrawable>> entrySet = mFocusDrawables.entrySet();
            for (Map.Entry<Class<? extends View>, FocusLampDrawable> entry : entrySet) {
                Class<? extends View> key = entry.getKey();
                if (view.getClass() == key) {
                    mCurDrawable = entry.getValue();
                    break;
                }
            }
            // Never set focus drawable, nothing to do
            if (mCurDrawable == null) {
                // If is dirty, clear the last focus drawable
                if (isDirty) {
                    invalidate();
                }
                return false;
            }
            return true;
        }

        // Check the focusView is in the area of this canvas
        private boolean isFocusInCanvas() {
            // If not set the drawArea, default drawArea is fullScreen
            if (mDrawArea == null) {
                return true;
            }
            return mCurrentRect.left >= mCanvasRect.left && mCurrentRect.right <= mCanvasRect.right &&
                    mCurrentRect.top >= mCanvasRect.top && mCurrentRect.bottom <= mCanvasRect.bottom;
        }

        void addDefaultFocusDrawable(FocusLampDrawable focusDrawable) {
            this.mDefaultDrawable = focusDrawable;
        }

        void addFocusDrawable(Class<? extends View> clazz, FocusLampDrawable drawable) {
            mFocusDrawables.put(clazz, drawable);
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

        // Draw focus drawable or clear canvas
        private void drawFocus(Canvas canvas) {
            if (isDirty && mCurDrawable == null) {
                canvas.drawColor(Color.TRANSPARENT);
                isDirty = false;
            } else if (mCurDrawable != null) {
                mCurDrawable.drawFocusFrame(canvas, mCurrentRect);
            }
        }
    }
}
