package io.github.droidkaigi.confsched2020.ui

import androidx.annotation.IdRes
import io.github.droidkaigi.confsched2020.R

enum class PageConfiguration(
    val id: Int,
    val isIndigoBackground: Boolean = false,
    val hasTitle: Boolean = true,
    val isShowLogoImage: Boolean = false,
    val hideToolbar: Boolean = false,
    val isTopLevel: Boolean = false
) {
    MAIN(R.id.main, isIndigoBackground = true, hasTitle = false, isShowLogoImage = true, isTopLevel = true),
    DETAIL(R.id.session_detail, hasTitle = false, hideToolbar = true),
    SPEAKER(R.id.speaker, hasTitle = false),
    ANNOUNCEMENT(R.id.announcement, isTopLevel = true),
    ABOUT(R.id.about, isTopLevel = true),
    FLOOR_MAP(R.id.floormap, isTopLevel = true),
    SPONSORS(R.id.sponsors, isTopLevel = true),
    CONTRIBUTOR(R.id.contributor, isTopLevel = true),
    SETTING(R.id.setting, isTopLevel = true),
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