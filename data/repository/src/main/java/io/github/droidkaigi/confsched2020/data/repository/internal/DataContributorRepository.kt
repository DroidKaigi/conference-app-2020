package io.github.droidkaigi.confsched2020.data.repository.internal

import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.db.ContributorDatabase
import io.github.droidkaigi.confsched2020.data.db.entity.ContributorEntity
import io.github.droidkaigi.confsched2020.model.Contributor
import io.github.droidkaigi.confsched2020.model.ContributorContents
import io.github.droidkaigi.confsched2020.model.repository.ContributorRepository
import javax.inject.Inject

class DataContributorRepository @Inject constructor(
    private val api: DroidKaigiApi,
    private val contributorDatabase: ContributorDatabase
) : ContributorRepository {
    override suspend fun contributorContents(): ContributorContents =
        ContributorContents(contributorList())

    override suspend fun refresh() {
        val response = api.getContributorList()
        contributorDatabase.save(response)
    }

    private suspend fun contributorList() = contributorDatabase
        .contributorList()
        .map { it.toContributor() }
}

private fun ContributorEntity.toContributor(): Contributor =
    Contributor(
        id = id,
        name = name,
        iconUrl = iconUrl,
        profileUrl = profileUrl,
        type = type
    )
