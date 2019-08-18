package io.github.droidkaigi.confsched2020.data.db

import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import io.github.droidkaigi.confsched2020.data.db.entity.StaffEntity
import kotlinx.coroutines.flow.Flow

interface StaffDatabase {
    fun staffs(): Flow<List<StaffEntity>>
    suspend fun save(apiResponse: StaffResponse)
}
