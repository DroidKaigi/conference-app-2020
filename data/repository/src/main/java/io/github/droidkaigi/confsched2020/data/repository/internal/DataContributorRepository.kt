package io.github.droidkaigi.confsched2020.data.repository.internal

import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.db.ContributorDatabase
import io.github.droidkaigi.confsched2020.data.db.entity.ContributorEntity
import io.github.droidkaigi.confsched2020.model.Contributor
import io.github.droidkaigi.confsched2020.model.repository.ContributorRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataContributorRepository @Inject constructor(
    private val api: DroidKaigiApi,
    private val contributorDatabase: ContributorDatabase
) : ContributorRepository {

    override fun contributorContents(): Flow<List<Contributor>> =
        contributorList()

    override suspend fun refresh() {
        val response = api.getContributorList()
        contributorDatabase.save(response)
    }

    private fun contributorList(): Flow<List<Contributor>> =
        contributorDatabase
            .contributorList()
            .map {
                it.mapIndexed { index, entity ->
                    entity.toContributor(index + 1)
                }
            }
}

private fun ContributorEntity.toContributor(rankOrder: Int): Contributor =
    Contributor(
        id = id,
        name = name,
        iconUrl = iconUrl,
        profileUrl = profileUrl,
        rankOrder = rankOrder.toString()
    )
