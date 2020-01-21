package io.github.droidkaigi.confsched2020.model

enum class ExpandFilterState {
    EXPANDED,
    CHANGING,
    COLLAPSED;
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
