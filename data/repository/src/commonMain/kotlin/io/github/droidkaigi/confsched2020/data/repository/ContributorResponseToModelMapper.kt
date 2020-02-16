package io.github.droidkaigi.confsched2020.data.repository

import io.github.droidkaigi.confsched2020.data.api.response.ContributorResponse
import io.github.droidkaigi.confsched2020.model.Contributor
import io.github.droidkaigi.confsched2020.model.ContributorIndex

fun ContributorResponse.toModel(): List<ContributorIndex> {
    return contributors
        .sortedBy { it.username }
        .groupBy { it.username.first().toUpperCase() }
        .map { (index, contributors) ->
            ContributorIndex(
                index = index.toString(),
                contributors = contributors.map { contributor ->
                    Contributor(
                        id = contributor.id,
                        name = contributor.username,
                        iconUrl = contributor.iconUrl,
                        profileUrl = contributor.profileUrl,
                        rankOrder = contributor.sort.toString()
                    )
                }
            )
        }
        .sortedBy { it.index }
}
