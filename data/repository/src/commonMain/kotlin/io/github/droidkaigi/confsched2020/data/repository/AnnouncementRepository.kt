package io.github.droidkaigi.confsched2020.data.repository

import io.github.droidkaigi.confsched2020.model.Announcement
import kotlinx.coroutines.flow.Flow

interface AnnouncementRepository {
    fun announcements(): Flow<List<Announcement>>
    suspend fun refresh()
}
