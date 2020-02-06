package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import io.github.droidkaigi.confsched2020.data.api.response.StaffItemResponse
import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import io.github.droidkaigi.confsched2020.data.db.entity.StaffEntity
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
    val staffResponse = object : StaffResponse {
        override val staffs: List<StaffItemResponse>
            get() = listOf(object : StaffItemResponse {
                override val id: String? = staffContents.staffs.first().id
                override val name: String? = staffContents.staffs.first().name
                override val iconUrl: String? = staffContents.staffs.first().iconUrl
                override val profileUrl: String? = staffContents.staffs.first().profileUrl
            })
    }
    val staffEntity = object : StaffEntity {
        override var id: String = staffContents.staffs.first().id
        override var name: String = staffContents.staffs.first().name
        override var iconUrl: String? = staffContents.staffs.first().iconUrl
        override var profileUrl: String? = staffContents.staffs.first().profileUrl
    }
}
