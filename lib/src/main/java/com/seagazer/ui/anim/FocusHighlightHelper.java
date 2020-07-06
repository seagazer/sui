package com.seagazer.ui.anim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import com.seagazer.ui.R;
import com.seagazer.ui.util.Constants;
import com.seagazer.ui.util.DensityConverter;

/**
 * 焦点缩放动效辅助
 */
public class FocusHighlightHelper {

    /**
     * 聚焦动画
     *
     * @param view     当前执行动画的view
     * @param hasFocus 聚焦状态
     */
    public void animFocus(View view, boolean hasFocus) {
        int animId = hasFocus ? R.id.highlight : R.id.normal;
        float animValue = hasFocus ? 1.2f : 1.0f;
        float z = hasFocus ? DensityConverter.dp2px(view.getContext(), 6) : 0;
        AnimatorSet set;
        if ((set = (AnimatorSet) view.getTag(animId)) == null) {
            set = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", animValue);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", animValue);
            ObjectAnimator translationZ = ObjectAnimator.ofFloat(view, "translationZ", z);
            set.playTogether(scaleX, scaleY, translationZ);
            set.setDuration(Constants.ANIM_SHORT_DURATION);
        }
        if (set.isRunning()) {
            set.cancel();
        }
        set.start();
        view.setTag(animId, set);
    }

}
