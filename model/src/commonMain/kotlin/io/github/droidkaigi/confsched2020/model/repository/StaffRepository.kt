package io.github.droidkaigi.confsched2020.model.repository

import io.github.droidkaigi.confsched2020.model.StaffContents
import kotlinx.coroutines.flow.Flow

interface StaffRepository {
    fun staffs(): Flow<StaffContents>
    suspend fun refresh()
}
