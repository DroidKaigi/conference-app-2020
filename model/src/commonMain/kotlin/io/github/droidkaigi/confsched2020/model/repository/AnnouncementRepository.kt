package io.github.droidkaigi.confsched2020.model.repository

import io.github.droidkaigi.confsched2020.model.Announcement
import kotlinx.coroutines.flow.Flow

interface AnnouncementRepository {
    fun announcements(): Flow<List<Announcement>>
    suspend fun refresh()
}
