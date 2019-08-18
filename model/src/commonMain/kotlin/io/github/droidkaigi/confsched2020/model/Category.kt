package io.github.droidkaigi.confsched2020.model

@AndroidParcelize
data class Category(
    val id: Int,
    val name: LocaledString
) : AndroidParcel
