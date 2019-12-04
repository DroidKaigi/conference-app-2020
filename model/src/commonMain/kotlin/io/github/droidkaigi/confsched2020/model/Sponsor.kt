package io.github.droidkaigi.confsched2020.model

data class Sponsor(
    val id: Int,
    val plan: String,
    val planDetail: String,
    val company: Company,
    val hasBooth: Boolean
)
