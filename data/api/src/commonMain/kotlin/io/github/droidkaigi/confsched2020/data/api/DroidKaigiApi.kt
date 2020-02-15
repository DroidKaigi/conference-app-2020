package io.github.droidkaigi.confsched2020.data.api

import io.github.droidkaigi.confsched2020.data.api.parameter.LangParameter
import io.github.droidkaigi.confsched2020.data.api.response.AnnouncementListResponse
import io.github.droidkaigi.confsched2020.data.api.response.ContributorResponse
import io.github.droidkaigi.confsched2020.data.api.response.Response
import io.github.droidkaigi.confsched2020.data.api.response.SponsorListResponse
import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import kotlinx.coroutines.Deferred

interface DroidKaigiApi {
    suspend fun getSessions(): Response

    fun getSessions(callback: (response: Response) -> Unit, onError: (error: Exception) -> Unit)

    fun getSessionsAsync(): Deferred<Response>

    fun getAnnouncements(
        lang: LangParameter,
        callback: (response: AnnouncementListResponse) -> Unit,
        onError: (error: Exception) -> Unit
    )

    fun getAnnouncementsAsync(lang: LangParameter): Deferred<AnnouncementListResponse>

    suspend fun getSponsors(): SponsorListResponse

    fun getSponsors(
        callback: (response: SponsorListResponse) -> Unit,
        onError: (error: Exception) -> Unit
    )

    suspend fun getAnnouncements(lang: LangParameter): AnnouncementListResponse

    suspend fun getStaffs(): StaffResponse

    fun getContributorList(
        callback: (response: ContributorResponse) -> Unit,
        onError: (error: Exception) -> Unit
    )

    suspend fun getContributorList(): ContributorResponse
}
