package com.seagazer.ui.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemDecoration extends RecyclerView.ItemDecoration {

    private int mLeft, mTop, mRight, mBottom;

    public SimpleItemDecoration(int left, int top, int right, int bottom) {
        this.mLeft = left;
        this.mTop = top;
        this.mRight = right;
        this.mBottom = bottom;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = mLeft;
        outRect.top = mTop;
        outRect.right = mRight;
        outRect.bottom = mBottom;
    }
}
