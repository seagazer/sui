package com.seagazer.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.seagazer.lib.R;
import com.seagazer.lib.util.Logger;

/**
 * 海报视图基类
 * 提供海报图，标题，子标题结构的视图
 * +---------------------+
 * +                     +
 * +    ***      ***     +
 * +        image        +
 * +                     +
 * +       **  **        +
 * +         **          +
 * +---------------------+
 * +       title         +
 * +      subTitle       +
 * +---------------------+
 */
public abstract class BaseCardView extends CardView {
    protected int mHighLightColor;
    protected int mDimColor;
    protected int mTextColor;
    protected int mTextSize;
    protected ImageView mImage;
    protected View mTextContent;
    protected TextView mTitle;
    protected TextView mSubTitle;
    private boolean isAutoHighLight = true;

    public BaseCardView(@NonNull Context context) {
        this(context, null);
    }

    public BaseCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseCardView);
        mHighLightColor = ta.getColor(R.styleable.BaseCardView_highLightColor, getResources().getColor(R.color.gray_700));
        mDimColor = ta.getColor(R.styleable.BaseCardView_dimColor, getResources().getColor(R.color.gray_900));
        mTextColor = ta.getColor(R.styleable.BaseCardView_textColor, getResources().getColor(R.color.textDefault));
        mTextSize = ta.getDimensionPixelSize(R.styleable.BaseCardView_textSize, getResources().getDimensionPixelOffset(R.dimen.cardTitleSmall));
        ta.recycle();
        // inflate view
        LayoutInflater.from(context).inflate(getLayoutId(), this, true);
        mImage = findViewById(getImageId());
        mTextContent = findViewById(getTextContainerId());
        mTitle = findViewById(getTitleId());
        mSubTitle = findViewById(getSubTitleId());
        if (checkNonNull(mTextContent)) {
            mTextContent.setBackgroundColor(mDimColor);
        }
        if (checkNonNull(mTitle)) {
            mTitle.getPaint().setTextSize(mTextSize);
            mTitle.setTextColor(mTextColor);
        }
        setRadius(getResources().getDimensionPixelSize(R.dimen.cardCorner));
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
    }

    protected abstract int getLayoutId();

    protected abstract int getImageId();

    protected abstract int getTextContainerId();

    protected abstract int getTitleId();

    protected abstract int getSubTitleId();

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (isAutoHighLight) {
            // keep onFocusChangeListener for user
            if (gainFocus) {
                highlight();
            } else {
                dim();
            }
        }
    }

    private void highlight() {
        if (checkNonNull(mImage)) {
            if (mImage instanceof DimmerImageView) {
                ((DimmerImageView) mImage).highlight();
            }
        }
        if (checkNonNull(mSubTitle)) {
            mSubTitle.setVisibility(VISIBLE);
        }
        if (checkNonNull(mTextContent)) {
            mTextContent.setBackgroundColor(mHighLightColor);
        }
    }

    private void dim() {
        if (checkNonNull(mImage)) {
            if (mImage instanceof DimmerImageView) {
                ((DimmerImageView) mImage).dim();
            }
        }
        if (checkNonNull(mSubTitle)) {
            mSubTitle.setVisibility(GONE);
        }
        if (checkNonNull(mTextContent)) {
            mTextContent.setBackgroundColor(mDimColor);
        }
    }

    /**
     * 是否聚焦自动高亮
     *
     * @param autoHighLight
     */
    public void setAutoHighLight(boolean autoHighLight) {
        isAutoHighLight = autoHighLight;
    }

    /**
     * 设置标题
     *
     * @param title 主标题
     */
    public void setTitle(String title) {
        if (checkNonNull(mTitle)) {
            mTitle.setText(title);
        }
    }

    /**
     * 设置副标题
     *
     * @param title 副标题
     */
    public void setSubTitle(String title) {
        if (checkNonNull(mSubTitle)) {
            mSubTitle.setText(title);
        }
    }

    /**
     * 设置图片
     *
     * @param path 图片路径
     */
    public void setImage(String path) {
        if (checkNonNull(mImage)) {
            imageLoader(mImage, path);
        }
    }

    /**
     * 设置图片
     *
     * @param drawable 图片资源
     */
    public void setImage(Drawable drawable) {
        if (checkNonNull(mImage)) {
            mImage.setImageDrawable(drawable);
        }
    }

    /**
     * 自定义图片加载方式
     *
     * @param view      图片
     * @param imagePath 图片路径
     */
    protected abstract void imageLoader(ImageView view, String imagePath);

    private boolean checkNonNull(View view) {
        if (view == null) {
            Logger.e("Check the view is null !");
            return false;
        }
        return true;
    }
}
