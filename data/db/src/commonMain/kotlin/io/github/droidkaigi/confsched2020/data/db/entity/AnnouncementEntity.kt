package io.github.droidkaigi.confsched2020.data.db.entity

interface AnnouncementEntity {
    val id: Long
    val title: String
    val content: String
    val type: String
    val publishedAt: Long
    val lang: String
}
