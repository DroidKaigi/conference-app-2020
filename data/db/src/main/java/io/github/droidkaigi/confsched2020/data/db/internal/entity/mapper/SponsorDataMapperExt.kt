package io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper

import io.github.droidkaigi.confsched2020.data.api.response.SponsorItemResponse
import io.github.droidkaigi.confsched2020.data.db.internal.entity.SponsorEntityImpl

internal fun List<SponsorItemResponse>.toSponsorEntities(
    category: String,
    categoryIndex: Int
): List<SponsorEntityImpl> =
    mapIndexed { displayOrder, responseSponsor ->
        SponsorEntityImpl(
            responseSponsor.id,
            responseSponsor.name,
            responseSponsor.url,
            responseSponsor.image,
            category,
            categoryIndex,
            displayOrder
        )
    }
