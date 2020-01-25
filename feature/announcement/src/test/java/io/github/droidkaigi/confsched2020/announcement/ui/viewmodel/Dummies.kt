package io.github.droidkaigi.confsched2020.announcement.ui.viewmodel

import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2020.model.Announcement

object Dummies {
    val announcements = listOf(
        Announcement(
            id = 1,
            title = "title",
            content = "content",
            publishedAt = DateTime(2020, 2, 10, 10, 10, 10),
            type = Announcement.Type.NOTIFICATION
        ),
        Announcement(
            id = 2,
            title = "title",
            content = "content",
            publishedAt = DateTime(2020, 2, 10, 10, 10, 10),
            type = Announcement.Type.NOTIFICATION
        ),
        Announcement(
            id = 3,
            title = "title",
            content = "content",
            publishedAt = DateTime(2020, 2, 10, 10, 10, 10),
            type = Announcement.Type.NOTIFICATION
        )
    )
}
