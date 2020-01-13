package io.github.droidkaigi.confsched2020.data.api.response

interface AnnouncementResponse {
    val id: Long
    val title: String
    val content: String
    val type: String
    val publishedAt: String
    val language: String
}
