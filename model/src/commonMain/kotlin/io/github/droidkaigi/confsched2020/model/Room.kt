package io.github.droidkaigi.confsched2020.model

@AndroidParcelize
data class Room(
    val id: Int,
    val name: LocaledString,
    val sort: Int
) : AndroidParcel {
    companion object {
        val EXHIBITION = Room(
            id = 0,
            name = LocaledString(ja = "Exhibition", en = "Exhibition"),
            sort = 0
        )
    }
}
