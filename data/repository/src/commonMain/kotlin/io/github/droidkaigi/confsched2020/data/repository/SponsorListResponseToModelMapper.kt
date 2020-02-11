package io.github.droidkaigi.confsched2020.data.repository

import io.github.droidkaigi.confsched2020.data.api.response.SponsorListResponse
import io.github.droidkaigi.confsched2020.model.Company
import io.github.droidkaigi.confsched2020.model.LocaledString
import io.github.droidkaigi.confsched2020.model.Sponsor
import io.github.droidkaigi.confsched2020.model.SponsorCategory

fun SponsorListResponse.toModel(): List<SponsorCategory> {
    return sortedBy { sponsorResponse -> sponsorResponse.sort }
        .groupBy { sponsorResponse -> sponsorResponse.plan }
        .mapNotNull { (plan, sponsors) ->
            val category = SponsorCategory.Category.from(plan)
                ?: return@mapNotNull null
            SponsorCategory(
                category,
                sponsors.map { sponsor ->
                    Sponsor(
                        id = sponsor.id,
                        plan = sponsor.plan,
                        planDetail = sponsor.planDetail,
                        company = Company(
                            name = LocaledString(
                                ja = sponsor.companyName.ja,
                                en = sponsor.companyName.en
                            ),
                            url = sponsor.companyUrl,
                            logoUrl = sponsor.companyLogoUrl
                        ),
                        hasBooth = sponsor.hasBooth
                    )
                }
            )
        }
}
