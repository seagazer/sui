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
 * 针对焦点扩展的RecyclerView
 * 如果该RecyclerView拥有一个聚焦的View，该View自动保持最后绘制
 * 默认自动过滤过快的按键事件200ms(最多按键5次/s)
 * 通过调用{@link #setFocusMemory(boolean)}设置焦点记忆状态
 * 通过调用{@link #interceptFirstChild(int...)} 设置第一个Child的拦截事件方向
 * 通过调用{@link #interceptLastChild(int...)} 设置最后一个Child的拦截事件方向
 */
public class ExRecyclerView extends RecyclerView {
    private int mKeyDrop = 200;
    private int mFocusPosition;
    private boolean isFocusMemory = true;
    private Handler mKeyDropHandler = new Handler();
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
     * 设置是否焦点记忆
     *
     * @param focusMemory 是否焦点记忆
     */
    public void setFocusMemory(boolean focusMemory) {
        isFocusMemory = focusMemory;
    }

    /**
     * 获取当前焦点记忆索引
     *
     * @return 当前焦点索引
     */
    public int getFocusPosition() {
        return mFocusPosition;
    }

    /**
     * 设置焦点索引
     *
     * @param position 焦点索引
     */
    public void setFocusPosition(int position) {
        if (position >= 0 && position <= getAdapter().getItemCount() - 1) {
            mFocusPosition = position;
        }
    }

    /**
     * 设置抛弃按键时长
     *
     * @param time 按键防抖时长
     */
    public void setKeyDrop(int time) {
        this.mKeyDrop = time;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && mKeyDrop != 0) {
            if (mKeyDropHandler.hasMessages(0)) {
                return true;
            }
            mKeyDropHandler.sendEmptyMessageDelayed(0, mKeyDrop);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        mFocusPosition = getChildViewHolder(child).getAdapterPosition();
        Logger.d("focus = " + mFocusPosition);
    }

    /**
     * 恢复焦点
     */
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

    /**
     * 拦截第一个Child View
     *
     * @param directions 拦截按键事件方向
     */
    public void interceptFirstChild(int... directions) {
        mFirstInterceptDirections = directions;
    }

    /**
     * 拦截最后一个Child View
     *
     * @param directions 拦截按键事件方向
     */
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
            int focusIndex = indexOfChild(view);
            if (i == childCount - 1) {
                return focusIndex;
            } else if (i < focusIndex) {
                return i;
            } else {
                return i + 1;
            }
        }
    }
}