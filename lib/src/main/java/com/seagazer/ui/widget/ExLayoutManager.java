package com.seagazer.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * An extension layoutManager.
 * First you must give the itemView focusable, you can call {@link #setAlignCenter(boolean)} that the select item will auto scroll to the center if can scroll,
 * and call {@link #setAlignX(int)} or {@link #setAlignY(int)} to set the end coordinate that the select item will auto scroll to.
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
     * Set the end scrolled coordinate of select view
     *
     * @param alignX Horizontal coordinate of the end scrolled
     */
    public void setAlignX(@IntRange(from = 0) int alignX) {
        isAlignCenter = false;
        this.mAlignX = alignX;
    }

    /**
     * Set the end scrolled coordinate of select view
     *
     * @param alignY Vertical coordinate of the end scrolled
     */
    public void setAlignY(@IntRange(from = 0) int alignY) {
        isAlignCenter = false;
        this.mAlignY = alignY;
    }

    /**
     * If set true the select view will scroll to the center of parent when scroll end
     *
     * @param alignCenter Is auto scroll to the center of parent
     */
    public void setAlignCenter(boolean alignCenter) {
        isAlignCenter = alignCenter;
        if (alignCenter) {
            mAlignX = 0;
            mAlignY = 0;
        }
    }
}
