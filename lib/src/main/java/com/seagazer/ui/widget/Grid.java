package com.seagazer.ui.widget;

import androidx.recyclerview.widget.RecyclerView;

public class Grid<T extends BasePresenter> {
    private String mTitle;
    private T mPresenter;
    private boolean isAlignCenter;
    private boolean isFocusMemory;
    private int mIndex;
    private int mAlignCoordinate;
    private RecyclerView.ItemDecoration mItemDecoration;
    private int[] mFirstInterceptDirections;
    private int[] mLastInterceptDirections;

    public Grid(String title, T presenter) {
        this.mTitle = title;
        this.mPresenter = presenter;
    }

    public void setAlignCenter(boolean alignCenter) {
        this.isAlignCenter = alignCenter;
    }

    public boolean isAlignCenter() {
        return isAlignCenter;
    }

    public void setFocusMemory(boolean focusMemory) {
        isFocusMemory = focusMemory;
    }

    public boolean isFocusMemory() {
        return isFocusMemory;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }

    public T getPresenter() {
        return mPresenter;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setAlignCoordinate(int alignCoordinate) {
        this.isAlignCenter = false;
        this.mAlignCoordinate = alignCoordinate;
    }

    public int getAlignCoordinate() {
        return mAlignCoordinate;
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        this.mItemDecoration = itemDecoration;
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        return mItemDecoration;
    }

    public void interceptFirstChild(int... directions) {
        mFirstInterceptDirections = directions;
    }

    public void interceptLastChild(int... directions) {
        mLastInterceptDirections = directions;
    }

    public int[] getFirstInterceptDirections() {
        return mFirstInterceptDirections;
    }

    public int[] getLastInterceptDirections() {
        return mLastInterceptDirections;
    }
}
