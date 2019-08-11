package io.github.droidkaigi.confsched2020.data.db

import io.github.droidkaigi.confsched2020.data.api.response.AnnouncementResponse
import io.github.droidkaigi.confsched2020.data.db.entity.AnnouncementEntity

interface AnnouncementDatabase {
    suspend fun announcementsByLang(lang: String): List<AnnouncementEntity>
    suspend fun save(apiResponse: List<AnnouncementResponse>)
}
