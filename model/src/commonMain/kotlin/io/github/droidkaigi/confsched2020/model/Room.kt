package io.github.droidkaigi.confsched2020.model

@AndroidParcelize
data class Room(
    val id: Int,
    val name: LocaledString,
    val sort: Int
) : AndroidParcel {

    val roomType: RoomType?
        get() = RoomType.fromId(id)

    enum class RoomType(val id: Int) {
        EXHIBITION(12203),
        APP_BAR(11511),
        BACKDROP(11512),
        CARDS(11513),
        DIALOGS(11514),
        PICKERS(11515),
        SLIDERS(11516),
        TABS(11517);

        companion object {
            fun fromId(id: Int): RoomType? {
                return values().firstOrNull { it.id == id }
            }
        }
    }
}
