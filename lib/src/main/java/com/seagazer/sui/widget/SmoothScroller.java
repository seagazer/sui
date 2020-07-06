package com.seagazer.sui.widget;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class SmoothScroller extends Scroller {

    private int duration;

    public SmoothScroller(Context context) {
        super(context);
    }

    public SmoothScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public SmoothScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        if (this.duration == 0) {
            this.duration = duration;
        }
        super.startScroll(startX, startY, dx, dy, this.duration);
    }

    /**
     * 设置viewPager切换页面时间
     *
     * @param duration 页面切换动画时长
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 绑定viewPager
     *
     * @param viewPager 需要绑定的viewPager
     * @param duration  滚动动画时长
     */
    public void setupViewPager(ViewPager viewPager, int duration) {
        setDuration(duration);
        try {
            Field field = viewPager.getClass().getDeclaredField("mScroller");
            field.setAccessible(true);
            field.set(viewPager, this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
