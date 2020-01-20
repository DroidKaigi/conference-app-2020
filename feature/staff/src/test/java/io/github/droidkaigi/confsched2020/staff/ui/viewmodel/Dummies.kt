package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import io.github.droidkaigi.confsched2020.model.Staff
import io.github.droidkaigi.confsched2020.model.StaffContents

object Dummies {
    val staffContents = StaffContents(
        listOf(
            Staff(
                id = "0",
                name = "staff",
                iconUrl = null,
                profileUrl = null
            )
        )
    )
}
