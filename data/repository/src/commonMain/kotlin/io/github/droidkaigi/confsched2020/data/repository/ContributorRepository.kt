package io.github.droidkaigi.confsched2020.data.repository

import io.github.droidkaigi.confsched2020.model.ContributorContents

interface ContributorRepository {
    suspend fun contributorContents(): ContributorContents
    suspend fun refresh(): Unit
}
