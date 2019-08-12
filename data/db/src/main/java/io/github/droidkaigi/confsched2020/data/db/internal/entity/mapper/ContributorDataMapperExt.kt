package io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper

import io.github.droidkaigi.confsched2020.data.api.response.ContributorItemResponse
import io.github.droidkaigi.confsched2020.data.db.internal.entity.ContributorEntityImpl

internal fun List<ContributorItemResponse>.toContributorEntities(): List<ContributorEntityImpl> =
    mapIndexed { index, response ->
        response.toContributorEntities(index)
    }

internal fun ContributorItemResponse.toContributorEntities(index: Int): ContributorEntityImpl {
    return ContributorEntityImpl(
        id = id,
        name = name,
        iconUrl = iconUrl,
        profileUrl = profileUrl,
        type = type,
        order = index
    )
}
