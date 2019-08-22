package com.seagazer.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.seagazer.ui.R;

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
        mDimColor = getResources().getColor(R.color.colorDimLight);
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
