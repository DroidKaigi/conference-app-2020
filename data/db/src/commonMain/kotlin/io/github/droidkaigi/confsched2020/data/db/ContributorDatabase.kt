package io.github.droidkaigi.confsched2020.data.db

import io.github.droidkaigi.confsched2020.data.api.response.ContributorResponse
import io.github.droidkaigi.confsched2020.data.db.entity.ContributorEntity

interface ContributorDatabase {
    suspend fun contributorList(): List<ContributorEntity>
    suspend fun save(apiResponse: ContributorResponse)
}
