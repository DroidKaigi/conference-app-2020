package io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper

import io.github.droidkaigi.confsched2020.data.api.response.ContributorItemResponse
import io.github.droidkaigi.confsched2020.data.db.internal.entity.ContributorEntityImpl

internal fun List<ContributorItemResponse>.toContributorEntities(): List<ContributorEntityImpl> =
    map {
        it.toContributorEntities()
    }

internal fun ContributorItemResponse.toContributorEntities(): ContributorEntityImpl =
    ContributorEntityImpl(
        id = id,
        name = username,
        iconUrl = iconUrl,
        profileUrl = profileUrl,
        order = sort
    )
