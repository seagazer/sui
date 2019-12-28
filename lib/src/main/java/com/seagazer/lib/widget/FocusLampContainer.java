package com.seagazer.lib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A container can auto draw focusDrawable when focus changed.
 * If you don't want to use this viewGroup as the container, you can also use {@link FocusLampHelper} instead.
 * <p>
 * Call {@link #addDefaultFocusDrawable(FocusLampDrawable)} to setup a default focusDrawable.
 * <p>
 * Call {@link #addFocusDrawable(Class, FocusLampDrawable)} to add a focusDrawable and bind a class, when the newFocus as same as this Class,
 * it will draw this focusDrawable, not the default focusDrawable.
 */
public class FocusLampContainer extends FrameLayout implements ViewTreeObserver.OnGlobalFocusChangeListener, ViewTreeObserver.OnDrawListener {
    private FocusDrawer mFocusDrawer;
    private View mFocused;

    public FocusLampContainer(Context context) {
        this(context, null);
    }

    public FocusLampContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusLampContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFocusDrawer = new FocusDrawer(context);
        mFocusDrawer.setupParent(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(mFocusDrawer, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewTreeObserver vto = getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalFocusChangeListener(this);
            vto.addOnDrawListener(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ViewTreeObserver vto = getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeOnGlobalFocusChangeListener(this);
            vto.removeOnDrawListener(this);
        }
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (newFocus != null && mFocused != newFocus) {
            mFocused = newFocus;
        }
    }

    @Override
    public void onDraw() {
        if (mFocusDrawer != null && mFocused != null) {
            mFocusDrawer.drawFocusFrame(mFocused);
        }
    }

    /**
     * Set a default drawable to draw focus, when the focus view has no target clazz in the mapping
     *
     * @param drawable A drawable to draw focusHighLight
     */
    public void addDefaultFocusDrawable(FocusLampDrawable drawable) {
        if (mFocusDrawer != null) {
            mFocusDrawer.addDefaultFocusDrawable(drawable);
        }
    }

    /**
     * Set a drawable to draw focus, when the focus instanceOf this clazz
     *
     * @param clazz    The class of the focus view
     * @param drawable A drawable to draw focusHighLight
     */
    public void addFocusDrawable(Class<? extends View> clazz, FocusLampDrawable drawable) {
        if (mFocusDrawer != null) {
            mFocusDrawer.addFocusDrawable(clazz, drawable);
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
        private ViewGroup mParent;
        private boolean isInitParentLocation = false;
        private int mOffsetHorizontal;
        private int mOffsetVertical;

        FocusDrawer(Context context) {
            super(context);
            mCurrentRect = new Rect();
            mLastRect = new Rect();
        }

        void setupParent(ViewGroup parent) {
            mParent = parent;
        }

        void drawFocusFrame(View view) {
            if (checkFocusDrawableNotNull(view)) {
                if (!isInitParentLocation) {
                    isInitParentLocation = true;
                    // Calculate the coordinate of parent
                    int[] parentLocation = new int[2];
                    mParent.getLocationOnScreen(parentLocation);
                    mOffsetHorizontal = parentLocation[0] + mParent.getPaddingLeft();
                    mOffsetVertical = parentLocation[1] + mParent.getPaddingTop();
                }
                // Calculate the focusView of parent
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int width = view.getWidth();
                int height = view.getHeight();
                float scaleX = view.getScaleX();
                float scaleY = view.getScaleY();
                int offsetX = (int) (width * (scaleX - 1));
                int offsetY = (int) (height * (scaleY - 1));
                int left = location[0] - mOffsetHorizontal;
                int top = location[1] - mOffsetVertical;
                int right = left + width + offsetX;
                int bottom = top + height + offsetY;
                mCurrentRect.set(left, top, right, bottom);
                if (mLastRect.left == mCurrentRect.left && mLastRect.top == mCurrentRect.top &&
                        mLastRect.right == mCurrentRect.right && mLastRect.bottom == mCurrentRect.bottom) {
                    // End draw, break the draw circulation
                    return;
                }
                invalidate();
                isDirty = true;
                mLastRect.set(mCurrentRect);
            }
        }

        private boolean checkFocusDrawableNotNull(View view) {
            Set<Map.Entry<Class<? extends View>, FocusLampDrawable>> entrySet = mFocusDrawables.entrySet();
            mCurDrawable = mDefaultDrawable;
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

        void addDefaultFocusDrawable(FocusLampDrawable focusDrawable) {
            this.mDefaultDrawable = focusDrawable;
        }

        void addFocusDrawable(Class<? extends View> clazz, FocusLampDrawable drawable) {
            mFocusDrawables.put(clazz, drawable);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (isDirty && mCurDrawable == null) {
                canvas.drawColor(Color.TRANSPARENT);
                isDirty = false;
            } else if (mCurDrawable != null) {
                mCurDrawable.drawFocusFrame(canvas, mCurrentRect);
            }
        }
    }

}
