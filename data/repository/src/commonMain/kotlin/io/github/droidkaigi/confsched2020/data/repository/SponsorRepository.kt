package io.github.droidkaigi.confsched2020.data.repository

import io.github.droidkaigi.confsched2020.model.SponsorCategory

interface SponsorRepository {
    suspend fun sponsors(): List<SponsorCategory>
    suspend fun refresh()
}
