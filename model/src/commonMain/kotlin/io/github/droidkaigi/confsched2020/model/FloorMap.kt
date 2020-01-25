package io.github.droidkaigi.confsched2020.model

enum class FloorMap(val id: Int) {
    APP_BAR(11511),
    BACKDROP(11512),
    CARDS(11513),
    DIALOGS(11514),
    PICKERS(11515),
    SLIDERS(11516),
    TABS(11517);

    companion object {
        fun fromId(id: Int): FloorMap? {
            return values().firstOrNull { it.id == id }
        }
    }
}
