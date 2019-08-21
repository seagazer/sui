package com.seagazer.ui.anim;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * 动画辅助类
 */
public class AnimHelper {

    /**
     * View的背景色渐变动画
     *
     * @param target   目标View
     * @param duration 动画时长
     * @param colors   渐变色
     * @return 背景色渐变动画
     */
    public static Animator backgroundColorAnim(final View target, int duration, Object... colors) {
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), colors);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                target.setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        animator.setDuration(duration);
        animator.start();
        return animator;
    }

}
