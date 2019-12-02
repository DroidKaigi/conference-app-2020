package io.github.droidkaigi.confsched2020.data.db

import io.github.droidkaigi.confsched2020.data.api.response.AnnouncementResponse
import io.github.droidkaigi.confsched2020.data.db.entity.AnnouncementEntity
import kotlinx.coroutines.flow.Flow

interface AnnouncementDatabase {
    fun announcementsByLang(lang: String): Flow<List<AnnouncementEntity>>
    suspend fun save(apiResponse: List<AnnouncementResponse>)
}
