package io.github.droidkaigi.confsched2020.binding

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun View.showHide(show: Boolean) {
    isVisible = show
}
