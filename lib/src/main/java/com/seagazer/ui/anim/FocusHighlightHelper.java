package com.seagazer.ui.anim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import com.seagazer.ui.R;
import com.seagazer.ui.util.Constants;
import com.seagazer.ui.util.DensityConverter;


public class FocusHighlightHelper {

    public void animFocus(View card, boolean hasFocus) {
        int animId = hasFocus ? R.id.highlight : R.id.normal;
        float animValue = hasFocus ? 1.2f : 1.0f;
        float z = hasFocus ? DensityConverter.dp2px(card.getContext(), 6) : 0;
        float y = hasFocus ? DensityConverter.dp2px(card.getContext(), 10) : 0;
        AnimatorSet set;
        if ((set = (AnimatorSet) card.getTag(animId)) == null) {
            set = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, "scaleX", animValue);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, "scaleY", animValue);
            ObjectAnimator translationZ = ObjectAnimator.ofFloat(card, "translationZ", z);
            set.playTogether(scaleX, scaleY, translationZ);
            set.setDuration(Constants.ANIM_SHORT_DURATION);
        }
        if (set.isRunning()) {
            set.cancel();
        }
        set.start();
        card.setTag(animId, set);
    }

}
