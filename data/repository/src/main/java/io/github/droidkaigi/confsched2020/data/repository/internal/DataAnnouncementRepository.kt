package io.github.droidkaigi.confsched2020.data.repository.internal

import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.parameter.LangParameter
import io.github.droidkaigi.confsched2020.data.api.parameter.LangResponseValue
import io.github.droidkaigi.confsched2020.data.db.AnnouncementDatabase
import io.github.droidkaigi.confsched2020.model.Announcement
import io.github.droidkaigi.confsched2020.model.Lang
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.model.repository.AnnouncementRepository
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataAnnouncementRepository @Inject constructor(
    private val droidKaigiApi: DroidKaigiApi,
    private val announcementDatabase: AnnouncementDatabase
) : AnnouncementRepository {
    override fun announcements(): Flow<List<Announcement>> {
        return announcementDatabase
            .announcementsByLang(defaultLang().toResponseValue().value)
            .map { announcementList ->
                announcementList
                    .sortedByDescending { it.publishedAt }
                    .map {
                        Announcement(
                            id = it.id,
                            type = Announcement.Type.valueOf(it.type.toUpperCase(Locale.US)),
                            title = it.title,
                            publishedAt = DateTime(it.publishedAt),
                            content = it.content
                        )
                    }
            }
    }

    override suspend fun refresh() {
        val announcements = droidKaigiApi.getAnnouncements(defaultLang().toParameter())
        announcementDatabase.save(announcements)
    }

    private fun Lang.toResponseValue(): LangResponseValue {
        return when (this) {
            Lang.EN -> LangResponseValue.EN
            Lang.JA -> LangResponseValue.JP
        }
    }

    private fun Lang.toParameter(): LangParameter {
        return when (this) {
            Lang.EN -> LangParameter.EN
            Lang.JA -> LangParameter.JP
        }
    }
}
