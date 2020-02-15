package io.github.droidkaigi.confsched2020.session.ui.animation

import android.animation.ObjectAnimator
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import io.github.droidkaigi.confsched2020.ext.awaitEnd
import io.github.droidkaigi.confsched2020.ext.awaitNextLayout
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val POP_UP_DURATION: Long = 100

internal fun View.popUp(lifecycleCoroutineScope: LifecycleCoroutineScope) {
    val target = this

    lifecycleCoroutineScope.launch {
        target.isVisible = true
        target.awaitNextLayout()
        val popupHeight = (target.height / 2).toFloat()
        target.translationY = popupHeight

        val fadeIn = async {
            ObjectAnimator.ofFloat(
                target,
                View.ALPHA,
                1f
            ).run {
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
                duration = POP_UP_DURATION
                start()
                awaitEnd()
            }
        }

        fadeIn.await()
        up.await()
    }
}

internal fun View.dropOut(lifecycleCoroutineScope: LifecycleCoroutineScope) {
    val target = this

    lifecycleCoroutineScope.launch {
        val dropOutHeight = (target.height / 2).toFloat()

        val fadeOut = async {
            ObjectAnimator.ofFloat(
                target,
                View.ALPHA,
                0f
            ).run {
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
                duration = POP_UP_DURATION
                start()
                awaitEnd()
            }
        }

        fadeOut.await()
        down.await()
        target.isVisible = false
    }
}

