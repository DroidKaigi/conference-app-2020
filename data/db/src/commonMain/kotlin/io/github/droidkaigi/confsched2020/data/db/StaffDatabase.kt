package io.github.droidkaigi.confsched2020.data.db

import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import io.github.droidkaigi.confsched2020.data.db.entity.StaffEntity

interface StaffDatabase {
    suspend fun staffs(): List<StaffEntity>
    suspend fun save(apiResponse: StaffResponse)
}
