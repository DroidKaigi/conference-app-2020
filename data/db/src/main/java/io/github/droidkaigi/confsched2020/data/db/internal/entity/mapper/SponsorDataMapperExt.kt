package io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper

import io.github.droidkaigi.confsched2020.data.api.response.CompanyNameResponse
import io.github.droidkaigi.confsched2020.data.api.response.SponsorResponse
import io.github.droidkaigi.confsched2020.data.db.internal.entity.CompanyNameEntityImpl
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SponsorEntityImpl

internal fun List<SponsorResponse>.toSponsorEntities(): List<SponsorEntityImpl> =
    map {
        it.toSponsorEntityImpl()
    }

internal fun SponsorResponse.toSponsorEntityImpl(): SponsorEntityImpl {
    return SponsorEntityImpl(
        id = id,
        plan = plan,
        planDetail = planDetail,
        companyUrl = companyUrl,
        companyName = companyName.toCompanyNameEntityImpl(),
        companyLogoUrl = companyLogoUrl,
        hasBooth = hasBooth,
        sort = sort
    )
}

internal fun CompanyNameResponse.toCompanyNameEntityImpl(): CompanyNameEntityImpl {
    return CompanyNameEntityImpl(
        jaName = ja,
        enName = en
    )
}
