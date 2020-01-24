package io.github.droidkaigi.confsched2020.model.repository

import io.github.droidkaigi.confsched2020.model.Contributor
import kotlinx.coroutines.flow.Flow

interface ContributorRepository {
    fun contributorContents(): Flow<List<Contributor>>
    suspend fun refresh(): Unit
}
