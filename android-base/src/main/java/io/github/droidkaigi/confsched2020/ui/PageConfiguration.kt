package io.github.droidkaigi.confsched2020.ui

import androidx.annotation.IdRes
import io.github.droidkaigi.confsched2020.R

enum class PageConfiguration(
    val id: Int,
    val isIndigoBackground: Boolean = false,
    val hasTitle: Boolean = true,
    val isShowLogoImage: Boolean = false,
    val hideToolbar: Boolean = false
) {
    MAIN(R.id.main, isIndigoBackground = true, hasTitle = false, isShowLogoImage = true),
    DETAIL(R.id.session_detail, hasTitle = false, hideToolbar = true),
    SPEAKER(R.id.speaker, hasTitle = false),
    OTHER(0);

    operator fun component1() = id
    operator fun component2() = isIndigoBackground
    operator fun component3() = hasTitle
    operator fun component4() = isShowLogoImage

    companion object {
        fun getConfiguration(@IdRes id: Int): PageConfiguration {
            return PageConfiguration
                .values()
                .firstOrNull { it.id == id } ?: OTHER
        }
    }
}