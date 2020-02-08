package io.github.droidkaigi.confsched2020.data.db

import io.github.droidkaigi.confsched2020.data.api.response.ContributorResponse
import io.github.droidkaigi.confsched2020.data.db.entity.ContributorEntity
import kotlinx.coroutines.flow.Flow

interface ContributorDatabase {
    fun contributorList(): Flow<List<ContributorEntity>>
    suspend fun save(apiResponse: ContributorResponse)
}
