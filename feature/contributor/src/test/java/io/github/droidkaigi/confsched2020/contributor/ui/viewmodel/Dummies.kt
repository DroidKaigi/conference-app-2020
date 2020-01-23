package io.github.droidkaigi.confsched2020.contributor.ui.viewmodel

import io.github.droidkaigi.confsched2020.model.Contributor

object Dummies {
    val contributors = listOf(
        Contributor(
            id = 1,
            name = "name",
            iconUrl = "icon url",
            profileUrl = "profile url",
            rankOrder = "rank order"
        )
    )
}
