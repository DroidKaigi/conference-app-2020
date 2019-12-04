package io.github.droidkaigi.confsched2020.data.db

import io.github.droidkaigi.confsched2020.data.api.response.SponsorResponse
import io.github.droidkaigi.confsched2020.data.db.entity.SponsorEntity
import kotlinx.coroutines.flow.Flow

interface SponsorDatabase {
    fun sponsors(): Flow<List<SponsorEntity>>
    suspend fun saveSponsors(apiResponse: List<SponsorResponse>)
}
