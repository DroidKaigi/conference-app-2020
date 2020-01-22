package io.github.droidkaigi.confsched2020.data.repository.internal

import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.data.db.entity.StaffEntity
import io.github.droidkaigi.confsched2020.model.Staff
import io.github.droidkaigi.confsched2020.model.StaffContents
import io.github.droidkaigi.confsched2020.model.repository.StaffRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class DataStaffRepository @Inject constructor(
    private val api: DroidKaigiApi,
    private val staffDatabase: StaffDatabase
) : StaffRepository {

    override suspend fun refresh() {
        val response = api.getStaffs()
        staffDatabase.save(response)
    }

    override fun staffs() = staffDatabase
        .staffs()
        .map { StaffContents(it.map { staffEntity -> staffEntity.toStaff() }) }
}

private fun StaffEntity.toStaff(): Staff = Staff(id, name, iconUrl, profileUrl)
