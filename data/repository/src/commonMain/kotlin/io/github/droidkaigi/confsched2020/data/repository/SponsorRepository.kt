package io.github.droidkaigi.confsched2020.data.repository

import io.github.droidkaigi.confsched2020.model.SponsorPlan
import kotlinx.coroutines.flow.Flow

interface SponsorRepository {
    fun sponsors(): Flow<List<SponsorPlan>>
    suspend fun refresh()
}
