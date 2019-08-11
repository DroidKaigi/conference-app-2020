package io.github.droidkaigi.confsched2020.data.repository

import io.github.droidkaigi.confsched2020.model.StaffContents

interface StaffRepository {
    suspend fun staffContents(): StaffContents
    suspend fun refresh()
}
