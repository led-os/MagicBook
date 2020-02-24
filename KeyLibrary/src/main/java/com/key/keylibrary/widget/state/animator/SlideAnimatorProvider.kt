package com.key.keylibrary.widget.state.animator

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.key.keylibrary.widget.state.view.AnimatorProvider

/**
 * @author Nukc.
 */
class SlideAnimatorProvider : AnimatorProvider {
    override fun showAnimation(view: View): Animator {
        val set = AnimatorSet()
        set.playTogether(
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(view, "translationX", getDistance(view), 0f)
        )
        set.interpolator = AccelerateInterpolator()
        return set
    }

    override fun hideAnimation(view: View): Animator {
        val set = AnimatorSet()
        set.playTogether(
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(view, "translationX", 0f, getDistance(view))
        )
        set.interpolator = DecelerateInterpolator()
        return set
    }

    private fun getDistance(view: View): Float {
        val viewParent = view.parent
        return if (viewParent == null) {
            0f
        } else {
            (viewParent as View).width.toFloat()
        }
    }
}