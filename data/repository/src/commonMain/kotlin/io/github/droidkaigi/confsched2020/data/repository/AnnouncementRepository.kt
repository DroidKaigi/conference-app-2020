package io.github.droidkaigi.confsched2020.data.repository

import io.github.droidkaigi.confsched2020.model.Announcement

interface AnnouncementRepository {
    suspend fun announcements(): List<Announcement>
    suspend fun refresh()
}
