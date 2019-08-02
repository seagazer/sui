package com.seagazer.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.seagazer.ui.R;
import com.seagazer.ui.util.Logger;

public abstract class BaseCardView extends CardView {
    protected int mHighLightColor;
    protected int mDimColor;
    protected int mTextColor;
    protected int mTextSize;
    protected ImageView mImage;
    protected View mTextContent;
    protected TextView mTitle;
    protected TextView mSubTitle;

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
        LayoutInflater.from(context).inflate(getRootViewId(), this, true);
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

    abstract int getRootViewId();

    abstract int getImageId();

    abstract int getTextContainerId();

    abstract int getTitleId();

    abstract int getSubTitleId();

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        // keep onFocusChangeListener for user
        if (gainFocus) {
            highlight();
        } else {
            dim();
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

    public void setTitle(String title) {
        if (checkNonNull(mTitle)) {
            mTitle.setText(title);
        }
    }

    public void setSubTitle(String title) {
        if (checkNonNull(mSubTitle)) {
            mSubTitle.setText(title);
        }
    }

    public void setImage(String path) {
        if (checkNonNull(mImage)) {
            loadImage(mImage, path);
        }
    }

    abstract void loadImage(ImageView view, String imagePath);

    private boolean checkNonNull(View view) {
        if (view == null) {
            Logger.e("Check the view is null !");
            return false;
        }
        return true;
    }
}
