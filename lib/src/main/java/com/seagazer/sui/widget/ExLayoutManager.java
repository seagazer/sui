package com.seagazer.sui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 针对焦点扩展的LayoutManager
 * 首先必须设置ItemView为可聚焦状态，可以通过调用{@link #setAlignCenter(boolean)}让当前聚焦的ItemView始终保持中间滑动
 * 或者调用{@link #setAlignX(int)} or {@link #setAlignY(int)}设置当前聚焦的ItemView滑动保持位置
 */
public class ExLayoutManager extends LinearLayoutManager {
    private int mAlignX = 0;
    private int mAlignY = 0;
    private boolean isAlignCenter = false;

    public ExLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
        int childLeft = child.getLeft();
        int childTop = child.getTop();
        int dx = 0, dy = 0;
        if (getOrientation() == RecyclerView.HORIZONTAL) {
            if (isAlignCenter && mAlignX == 0) {
                dx = childLeft - (parent.getWidth() - child.getWidth()) / 2;
            } else if (mAlignX < parent.getRight()) {
                dx = childLeft - mAlignX;
            } else {
                dx = 0;
            }
        } else {
            if (isAlignCenter && mAlignY == 0) {
                dy = childTop - (parent.getHeight() - child.getHeight()) / 2;
            } else if (mAlignY < parent.getBottom()) {
                dy = childTop - mAlignY;
            } else {
                dy = 0;
            }
        }
        if (!focusedChildVisible) {
            if (dx != 0 || dy != 0) {
                // dx<0:-->,  dx>0:<--
                if (immediate) {
                    parent.scrollBy(dx, dy);
                } else {
                    parent.smoothScrollBy(dx, dy);
                }
                return true;
            }
        }
        return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
    }

    /**
     * 设置当前聚焦View的水平滑动保持位置
     *
     * @param alignX 水平滑动保持位置px
     */
    public void setAlignX(@IntRange(from = 0) int alignX) {
        isAlignCenter = false;
        this.mAlignX = alignX;
    }

    /**
     * 设置当前聚焦View的垂直滑动保持位置
     *
     * @param alignY 垂直滑动保持位置px
     */
    public void setAlignY(@IntRange(from = 0) int alignY) {
        isAlignCenter = false;
        this.mAlignY = alignY;
    }

    /**
     * 设置当前聚焦View的滑动后保持中间位置
     *
     * @param alignCenter 是否保持居中位置
     */
    public void setAlignCenter(boolean alignCenter) {
        isAlignCenter = alignCenter;
        if (alignCenter) {
            mAlignX = 0;
            mAlignY = 0;
        }
    }
}
