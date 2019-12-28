package com.seagazer.lib.widget;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 行或者列的视图提供者
 *
 * @param <T> 视图及内容绑定提供者
 */
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
    private int mTitleColor;
    private int mTitleSize;

    public Grid(String title, T presenter) {
        this.mTitle = title;
        this.mPresenter = presenter;
    }

    public int getTitleColor() {
        return mTitleColor;
    }

    /**
     * 设置分类标题颜色
     *
     * @param color 分类标题颜色
     */
    public void setTitleColor(int color) {
        this.mTitleColor = color;
    }

    public int getTitleSize() {
        return mTitleSize;
    }

    /**
     * 设置分类标题字体大小
     *
     * @param size 标题字体大小
     */
    public void setTitleSize(int size) {
        this.mTitleSize = size;
    }

    /**
     * 设置是否居中聚焦
     *
     * @param alignCenter 是否居中聚焦
     */
    public void setAlignCenter(boolean alignCenter) {
        this.isAlignCenter = alignCenter;
    }

    /**
     * @return 是否居中聚焦
     */
    public boolean isAlignCenter() {
        return isAlignCenter;
    }

    /**
     * 是否记忆焦点
     *
     * @param focusMemory 是否记忆焦点
     */
    public void setFocusMemory(boolean focusMemory) {
        isFocusMemory = focusMemory;
    }

    /**
     * @return 是否记忆焦点
     */
    public boolean isFocusMemory() {
        return isFocusMemory;
    }

    /**
     * 设置行或列的索引
     *
     * @param index 行或列的索引值
     */
    public void setIndex(int index) {
        this.mIndex = index;
    }

    /**
     * @return 当前行或列的索引值
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * @return 行或列的视图提供者
     */
    public T getPresenter() {
        return mPresenter;
    }

    /**
     * @return 行或列的标题
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * 设置聚焦对齐坐标
     *
     * @param alignCoordinate 对齐坐标
     */
    public void setAlignCoordinate(int alignCoordinate) {
        this.isAlignCenter = false;
        this.mAlignCoordinate = alignCoordinate;
    }

    /**
     * @return 对齐坐标
     */
    public int getAlignCoordinate() {
        return mAlignCoordinate;
    }

    /**
     * 设置装饰边距
     *
     * @param itemDecoration 装饰边距
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        this.mItemDecoration = itemDecoration;
    }

    /**
     * @return 装饰边距
     */
    public RecyclerView.ItemDecoration getItemDecoration() {
        return mItemDecoration;
    }

    /**
     * {@link ExRecyclerView#interceptFirstChild(int...)}
     */
    public void interceptFirstChild(int... directions) {
        mFirstInterceptDirections = directions;
    }

    /**
     * {@link ExRecyclerView#interceptLastChild(int...)}
     */
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
