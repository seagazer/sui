package com.seagazer.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
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
 * <p>
 * Call {@link #addDefaultFocusDrawable(FocusDrawable)} to setup a default focusDrawable.
 * Call {@link #addFocusDrawable(Class, FocusDrawable)} (FocusDrawable)} to add a focusDrawable,
 * when the newFocus instance this Class then will draw this drawable.
 */
public class FocusFrameContainer extends FrameLayout implements ViewTreeObserver.OnGlobalFocusChangeListener, ViewTreeObserver.OnDrawListener {
    private FocusDrawer mFocusDrawer;
    private View mFocused;

    public FocusFrameContainer(Context context) {
        this(context, null);
    }

    public FocusFrameContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusFrameContainer(Context context, AttributeSet attrs, int defStyleAttr) {
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
    public void addDefaultFocusDrawable(FocusDrawable drawable) {
        if (mFocusDrawer != null) {
            mFocusDrawer.addDefaultFocusDrawable(drawable);
        }
    }

    /**
     * Set a drawable to draw focus, when the focus instanceOf this clazz
     *
     * @param clazz    The class instanceOf the focus view
     * @param drawable A drawable to draw focusHighLight
     */
    public void addFocusDrawable(Class<? extends View> clazz, FocusDrawable drawable) {
        if (mFocusDrawer != null) {
            mFocusDrawer.addFocusDrawable(clazz, drawable);
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
                    // calculate the coordinate of parent
                    int[] parentLocation = new int[2];
                    mParent.getLocationOnScreen(parentLocation);
                    // the offset of parent on the screen
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // if not clip padding, not calculate the parent's padding
                        if (!mParent.getClipToPadding()) {
                            mOffsetHorizontal = parentLocation[0];
                            mOffsetVertical = parentLocation[1];
                        } else {
                            mOffsetHorizontal = parentLocation[0] + mParent.getPaddingLeft();
                            mOffsetVertical = parentLocation[1] + mParent.getPaddingTop();
                        }
                    } else {
                        mOffsetHorizontal = parentLocation[0] + mParent.getPaddingLeft();
                        mOffsetVertical = parentLocation[1] + mParent.getPaddingTop();
                    }
                }
                // calculate the focusView of parent
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
                    // end draw, break the draw circulation
                    return;
                }
                invalidate();
                isDirty = true;
                mLastRect.set(mCurrentRect);
            }
        }

        private boolean checkFocusDrawableNotNull(View view) {
            Set<Map.Entry<Class<? extends View>, FocusDrawable>> entrySet = mFocusDrawables.entrySet();
            mCurDrawable = mDefaultDrawable;
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
