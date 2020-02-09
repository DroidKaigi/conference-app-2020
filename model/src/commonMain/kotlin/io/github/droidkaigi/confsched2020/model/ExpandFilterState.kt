package io.github.droidkaigi.confsched2020.model

sealed class ExpandFilterState {
    object EXPANDED : ExpandFilterState()
    object COLLAPSED : ExpandFilterState()
    data class CHANGING(val isCollapse: Boolean) : ExpandFilterState()

    fun toggledState(): ExpandFilterState {
        if (this == EXPANDED) {
            return COLLAPSED
        }
        if (this == COLLAPSED) {
            return EXPANDED
        }
        return this
    }
}
