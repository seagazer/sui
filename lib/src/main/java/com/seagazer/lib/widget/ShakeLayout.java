package com.seagazer.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.seagazer.lib.R;
import com.seagazer.lib.util.Logger;

/**
 * 边侧自动执行抖动动效的布局，一般用于根布局
 * 当执行上下左右按键，并且无法继续移动焦点时，自动开启左右或者上下抖动动效
 */
public class ShakeLayout extends FrameLayout {

    private Animation mShakeX, mShakeY;
    private CycleInterpolator mInterpolator;

    public ShakeLayout(@NonNull Context context) {
        super(context);
        mInterpolator = new CycleInterpolator(2);
    }

    public ShakeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mInterpolator = new CycleInterpolator(2);
    }

    public ShakeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInterpolator = new CycleInterpolator(2);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        View focusSearch = super.focusSearch(focused, direction);
        if (focusSearch == null) {
            Logger.d("next focus is null");
            if (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) {
                if (mShakeX == null) {
                    mShakeX = AnimationUtils.loadAnimation(getContext(), R.anim.shake_x);
                    mShakeX.setInterpolator(mInterpolator);
                }
                focused.startAnimation(mShakeX);
            } else if (direction == View.FOCUS_UP || direction == View.FOCUS_DOWN) {
                if (mShakeY == null) {
                    mShakeY = AnimationUtils.loadAnimation(getContext(), R.anim.shake_y);
                    mShakeY.setInterpolator(mInterpolator);
                }
                focused.startAnimation(mShakeY);
            }
        }
        return focusSearch;
    }
}
