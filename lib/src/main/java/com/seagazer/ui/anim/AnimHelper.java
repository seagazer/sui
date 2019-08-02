package com.seagazer.ui.anim;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.View;

public class AnimHelper {

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
