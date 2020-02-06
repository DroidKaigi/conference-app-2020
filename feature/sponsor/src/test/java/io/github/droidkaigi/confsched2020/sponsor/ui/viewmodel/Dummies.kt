package io.github.droidkaigi.confsched2020.sponsor.ui.viewmodel

import io.github.droidkaigi.confsched2020.model.Company
import io.github.droidkaigi.confsched2020.model.LocaledString
import io.github.droidkaigi.confsched2020.model.Sponsor
import io.github.droidkaigi.confsched2020.model.SponsorCategory

object Dummies {
    val sponsors = listOf(
        SponsorCategory(
            SponsorCategory.Category.PLATINUM,
            listOf(
                Sponsor(
                    1,
                    "plan",
                    "plan detail",
                    Company(
                        LocaledString("JA Company", "EN Company"),
                        "url",
                        "logo url"
                    ),
                    true
                )
            ))
    )
}
