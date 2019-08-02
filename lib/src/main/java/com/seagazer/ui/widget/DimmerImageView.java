package com.seagazer.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.seagazer.ui.R;

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

    public void highlight() {
        clearColorFilter();
    }

    public void dim() {
        setColorFilter(mDimColor);
    }

}
