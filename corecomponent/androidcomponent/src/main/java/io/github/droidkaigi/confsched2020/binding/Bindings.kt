package io.github.droidkaigi.confsched2020.binding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import io.github.droidkaigi.confsched2020.util.AndroidRInteger

@BindingAdapter("isVisible")
fun View.showGone(show: Boolean) {
    isVisible = show
}

@BindingAdapter("isInvisible")
fun View.showHide(invisible: Boolean) {
    isInvisible = invisible
}

@BindingAdapter("isVisibleWithAnimation")
fun View.showGoneWithAnimation(show: Boolean) {
    val animationDuration = resources.getInteger(AndroidRInteger.config_longAnimTime)
    val endOpacity = if (show) 100f else 0f
    val endVisible = if (show) View.VISIBLE else View.GONE
    animate()
        .alpha(endOpacity)
        .setDuration(animationDuration.toLong())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = endVisible
            }
        })
}

@BindingAdapter("isHideWithAnimation")
fun View.showHideWithAnimation(show: Boolean) {
    val animationDuration = resources.getInteger(AndroidRInteger.config_longAnimTime)
    val endOpacity = if (show) 100f else 0f
    val endVisible = if (show) View.VISIBLE else View.INVISIBLE
    animate()
        .alpha(endOpacity)
        .setDuration(animationDuration.toLong())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = endVisible
            }
        })
}