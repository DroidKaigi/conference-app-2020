package io.github.droidkaigi.confsched2020.ext

import androidx.core.widget.ContentLoadingProgressBar

fun ContentLoadingProgressBar.showHide(show: Boolean) {
    if (show) show() else hide()
}
