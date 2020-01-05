package io.github.droidkaigi.confsched2020.model

@AndroidParcelize
data class Room(
    val id: Int,
    val name: LocaledString,
    val sort: Int
) : AndroidParcel
