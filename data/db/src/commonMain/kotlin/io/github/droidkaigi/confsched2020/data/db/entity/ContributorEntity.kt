package io.github.droidkaigi.confsched2020.data.db.entity

interface ContributorEntity {
    val id: Int
    val name: String
    val iconUrl: String
    val profileUrl: String
    val order: Int
}
