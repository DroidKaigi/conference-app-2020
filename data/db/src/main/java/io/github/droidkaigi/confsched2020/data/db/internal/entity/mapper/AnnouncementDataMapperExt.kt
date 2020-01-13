package io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper

import com.soywiz.klock.DateFormat
import com.soywiz.klock.parse
import io.github.droidkaigi.confsched2020.data.api.response.AnnouncementResponse
import io.github.droidkaigi.confsched2020.data.db.internal.entity.AnnouncementEntityImpl
import java.util.Locale

private val dateFormat: DateFormat =
    DateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

internal fun List<AnnouncementResponse>.toAnnouncementEntities(): List<AnnouncementEntityImpl> =
    map {
        it.toAnnouncementEntityImpl()
    }

internal fun AnnouncementResponse.toAnnouncementEntityImpl(): AnnouncementEntityImpl {
    return AnnouncementEntityImpl(
        id = id,
        content = content,
        publishedAt = dateFormat.parse(publishedAt).utc.unixMillisLong,
        lang = language,
        title = title,
        type = type.toLowerCase(Locale.US)
    )
}
