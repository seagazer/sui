package com.seagazer.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.seagazer.lib.R;

/**
 * 带有遮罩层的ImageView
 */
public class DimmerImageView extends AppCompatImageView {
    private int mDimColor;

    public DimmerImageView(Context context) {
        this(context, null);
    }

    public DimmerImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DimmerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DimmerImageView);
        mDimColor = ta.getColor(R.styleable.DimmerImageView_filterColor, getResources().getColor(R.color.colorDimLight));
        ta.recycle();
        dim();
    }

    /**
     * 设置遮罩阴影颜色
     *
     * @param color 遮罩颜色
     */
    public void setDimColor(int color) {
        this.mDimColor = color;
    }

    /**
     * 高亮显示
     */
    public void highlight() {
        clearColorFilter();
    }

    /**
     * 昏暗显示
     */
    public void dim() {
        setColorFilter(mDimColor);
    }

}
