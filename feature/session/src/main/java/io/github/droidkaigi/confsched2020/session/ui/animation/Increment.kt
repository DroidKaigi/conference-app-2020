package io.github.droidkaigi.confsched2020.session.ui.animation

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import io.github.droidkaigi.confsched2020.ext.awaitEnd
import io.github.droidkaigi.confsched2020.ext.awaitNextLayout
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// The view is shown with fade-in and move-up animation.
internal fun View.popUp(lifecycleCoroutineScope: LifecycleCoroutineScope) {
    val target = this

    lifecycleCoroutineScope.launch {
        target.isVisible = true
        target.awaitNextLayout()
        val popupHeight = (target.height / 3).toFloat()
        target.translationY = popupHeight

        val fadeIn = async {
            target.alpha = 0f
            ObjectAnimator.ofFloat(
                target,
                View.ALPHA,
                1f
            ).run {
                interpolator = DecelerateInterpolator()
                start()
                awaitEnd()
            }
        }

        val up = async {
            ObjectAnimator.ofFloat(
                target,
                View.TRANSLATION_Y,
                -popupHeight
            ).run {
                interpolator = DecelerateInterpolator()
                duration = UP_DOWN_DURATION
                start()
                awaitEnd()
            }
        }

        fadeIn.await()
        up.await()
    }
}

// The view is hidden with fade-out and move-down animation.
internal fun View.dropOut(lifecycleCoroutineScope: LifecycleCoroutineScope) {
    val target = this

    lifecycleCoroutineScope.launch {
        val dropOutHeight = (target.height / 3).toFloat()

        val fadeOut = async {
            ObjectAnimator.ofFloat(
                target,
                View.ALPHA,
                0f
            ).run {
                interpolator = AccelerateInterpolator()
                duration = 100
                start()
                awaitEnd()
            }
        }

        val down = async {
            ObjectAnimator.ofFloat(
                target,
                View.TRANSLATION_Y,
                dropOutHeight
            ).run {
                interpolator = AccelerateInterpolator()
                duration = UP_DOWN_DURATION
                start()
                awaitEnd()
            }
        }

        fadeOut.await()
        down.await()
        target.isVisible = false
    }
}

// The view is scale up and down at current position.
internal fun View.pop(lifecycleCoroutineScope: LifecycleCoroutineScope) {
    val target = this
    target.scaleX
    val scaleValue = 0.2f
    val scaleXAnimator = { x: Float ->
        ObjectAnimator
            .ofFloat(target, View.SCALE_X, x)
            .also {
                it.interpolator = DecelerateInterpolator()
                it.duration = SCALE_DURATION
            }
    }
    val scaleYAnimator = { y: Float ->
        ObjectAnimator
            .ofFloat(target, View.SCALE_Y, y)
            .also {
                it.interpolator = DecelerateInterpolator()
                it.duration = SCALE_DURATION
            }
    }

    lifecycleCoroutineScope.launch {
        val scaleXUp = async {
            scaleXAnimator(1f + scaleValue).run {
                start()
                awaitEnd()
            }
        }
        val scaleYUp = async {
            scaleYAnimator(1f + scaleValue).run {
                start()
                awaitEnd()
            }
        }
        scaleXUp.await()
        scaleYUp.await()

        val scaleXDown = async {
            scaleXAnimator(1f - scaleValue).run {
                start()
                awaitEnd()
            }
        }
        val scaleYDown = async {
            scaleYAnimator(1f - scaleValue).run {
                start()
                awaitEnd()
            }
        }
        scaleXDown.await()
        scaleYDown.await()
    }
}

private const val UP_DOWN_DURATION: Long = 100
private const val SCALE_DURATION: Long = 100
