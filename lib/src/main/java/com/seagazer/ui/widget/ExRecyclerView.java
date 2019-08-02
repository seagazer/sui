package com.seagazer.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.seagazer.ui.util.Logger;

import java.util.ArrayList;

/**
 * An extension recyclerView.
 * If this recyclerView has a focused child, the child will be draw last.
 * Use {@link #setFocusMemory(boolean)} to cache the focused view when recyclerView lost focus,
 * then it can resume the focus if it gainFocus again.
 */
public class ExRecyclerView extends RecyclerView {
    private static final int KEY_DROP = 100;
    private int mFocusPosition;
    private boolean isFocusMemory = true;
    private Handler mKey = new Handler();
    private int[] mFirstInterceptDirections;
    private int[] mLastInterceptDirections;

    public ExRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public ExRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setChildrenDrawingOrderEnabled(true);
    }

    /**
     * Set true to cache the focused view when recyclerView lost focus
     *
     * @param focusMemory Cache the focused or not
     */
    public void setFocusMemory(boolean focusMemory) {
        isFocusMemory = focusMemory;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mKey.hasMessages(0)) {
                return true;
            }
            mKey.sendEmptyMessageDelayed(0, KEY_DROP);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        mFocusPosition = getChildViewHolder(child).getAdapterPosition();
        Logger.d("focus = " + mFocusPosition);
    }

    public void resumeFocus() {
        if (getLayoutManager() != null) {
            View focused = getLayoutManager().findViewByPosition(mFocusPosition);
            if (focused != null) {
                focused.requestFocus();
            }
        }
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (isFocusMemory) {
            if (!hasFocus() && getLayoutManager() != null) {
                View focused = getLayoutManager().findViewByPosition(mFocusPosition);
                if (focused != null) {
                    views.add(focused);
                    return;
                }
            }
        }
        super.addFocusables(views, direction, focusableMode);
    }

    public void interceptFirstChild(int... directions) {
        mFirstInterceptDirections = directions;
    }

    public void interceptLastChild(int... directions) {
        mLastInterceptDirections = directions;
    }

    @Override
    public View focusSearch(View focused, int direction) {
        int position = getChildAdapterPosition(focused);
        if (position == 0 && mFirstInterceptDirections != null && mFirstInterceptDirections.length > 0) {
            for (int dir : mFirstInterceptDirections) {
                if (direction == dir) {
                    Logger.d("intercept the first child, direction = " + direction);
                    return focused;
                }
            }
        }
        if (getAdapter() != null && position == getAdapter().getItemCount() - 1 &&
                mLastInterceptDirections != null && mLastInterceptDirections.length > 0) {
            for (int dir : mLastInterceptDirections) {
                if (direction == dir) {
                    Logger.d("intercept the last child, direction = " + direction);
                    return focused;
                }
            }
        }
        return super.focusSearch(focused, direction);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) {
            return i;
        }
        View view = layoutManager.findViewByPosition(mFocusPosition);
        if (view == null) {
            return i;
        } else {
            int index = indexOfChild(view);
            if (i == childCount - 1) {
                return index;
            } else if (i < index) {
                return i;
            } else {
                return i + 1;
            }
        }
    }
}