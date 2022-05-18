package ir.mohammadhf.birthdays.utils

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import javax.inject.Inject

class AnimationFactory @Inject constructor() {

    val fadeInQuick: Animation
        get() = AlphaAnimation(0F, 1F).apply {
            duration = 100
            fillAfter = false
        }

    val fadeOutQuick: Animation
        get() = AlphaAnimation(1F, 0F).apply {
            duration = 100
            fillAfter = false
        }
}