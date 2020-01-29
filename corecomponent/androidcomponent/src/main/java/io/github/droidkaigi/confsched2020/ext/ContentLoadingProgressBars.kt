package io.github.droidkaigi.confsched2020.ext

import androidx.core.widget.ContentLoadingProgressBar

const val NO_GET = "Property does not have a 'get'"
inline var ContentLoadingProgressBar.isShow: Boolean
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from ContentLoadingProgressBar")
    set(value) {
        if (value) show() else hide()
    }
