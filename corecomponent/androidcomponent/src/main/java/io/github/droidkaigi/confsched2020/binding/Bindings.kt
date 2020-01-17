package io.github.droidkaigi.confsched2020.binding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun View.showGone(show: Boolean) {
    isVisible = show
}

@BindingAdapter("isHide")
fun View.showHide(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("isVisibleWithAnimation")
fun View.showGoneWithAnimation(show: Boolean) {
    val shortAnimationDuration = resources.getInteger(android.R.integer.config_longAnimTime)
    val endOpacity = if (show) 100f else 0f
    val endVisible = if (show) View.VISIBLE else View.GONE
    animate()
        .alpha(endOpacity)
        .setDuration(shortAnimationDuration.toLong())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = endVisible
            }
        })
}

@BindingAdapter("isHideWithAnimation")
fun View.showHideWithAnimation(show: Boolean) {
    val shortAnimationDuration = resources.getInteger(android.R.integer.config_longAnimTime)
    val endOpacity = if (show) 100f else 0f
    val endVisible = if (show) View.VISIBLE else View.INVISIBLE
    animate()
        .alpha(endOpacity)
        .setDuration(shortAnimationDuration.toLong())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = endVisible
            }
        })
}