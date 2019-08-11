package io.github.droidkaigi.confsched2020.data.db

import io.github.droidkaigi.confsched2020.data.api.response.SponsorResponse
import io.github.droidkaigi.confsched2020.data.db.entity.SponsorEntity

interface SponsorDatabase {
    suspend fun sponsors(): List<SponsorEntity>
    suspend fun save(apiResponse: SponsorResponse)
}
