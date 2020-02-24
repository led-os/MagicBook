package com.key.keylibrary.widget.state.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import com.key.keylibrary.widget.state.view.AnimatorProvider;


/**
 * @author Nukc.
 */

public class FadeScaleAnimatorProvider implements AnimatorProvider {

    @Override
    public Animator showAnimation(View view) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(view, "scaleX", 0.1f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.1f, 1f)
        );
        return set;
    }

    @Override
    public Animator hideAnimation(View view) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.1f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.1f)
        );
        return set;
    }
}
