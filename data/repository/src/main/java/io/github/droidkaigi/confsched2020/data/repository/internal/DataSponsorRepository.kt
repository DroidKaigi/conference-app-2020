package io.github.droidkaigi.confsched2020.data.repository.internal

import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2020.data.db.entity.SponsorEntity
import io.github.droidkaigi.confsched2020.model.Company
import io.github.droidkaigi.confsched2020.model.LocaledString
import io.github.droidkaigi.confsched2020.model.Sponsor
import io.github.droidkaigi.confsched2020.model.SponsorCategory
import io.github.droidkaigi.confsched2020.model.repository.SponsorRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataSponsorRepository @Inject constructor(
    private val api: DroidKaigiApi,
    private val sponsorDatabase: SponsorDatabase
) : SponsorRepository {
    override fun sponsors(): Flow<List<SponsorCategory>> {
        return sponsorDatabase
            .sponsors()
            .map {
                it.sortedBy { sponsorEntity -> sponsorEntity.sort }
                    .groupBy { sponsorEntity -> sponsorEntity.plan }
                    .mapNotNull { (plan, sponsors) ->
                        val category = SponsorCategory.Category.from(plan)
                            ?: return@mapNotNull null
                        SponsorCategory(
                            category,
                            sponsors.map(SponsorEntity::toSponsor)
                        )
                    }
            }
    }

    override suspend fun refresh() {
        val response = api.getSponsors()
        sponsorDatabase.saveSponsors(response)
    }
}

private fun SponsorEntity.toSponsor(): Sponsor = Sponsor(
    id = id,
    plan = plan,
    planDetail = planDetail,
    company = Company(
        url = companyUrl,
        name = LocaledString(
            companyName.jaName,
            companyName.enName
        ),
        logoUrl = companyLogoUrl
    ),
    hasBooth = hasBooth
)
