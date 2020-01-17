package io.github.droidkaigi.confsched2020.data.api.internal

import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.internal.response.AnnouncementResponseImpl
import io.github.droidkaigi.confsched2020.data.api.internal.response.ContributorItemResponseImpl
import io.github.droidkaigi.confsched2020.data.api.internal.response.ContributorResponseImpl
import io.github.droidkaigi.confsched2020.data.api.internal.response.ResponseImpl
import io.github.droidkaigi.confsched2020.data.api.internal.response.SponsorResponseImpl
import io.github.droidkaigi.confsched2020.data.api.internal.response.StaffItemResponseImpl
import io.github.droidkaigi.confsched2020.data.api.internal.response.StaffResponseImpl
import io.github.droidkaigi.confsched2020.data.api.parameter.LangParameter
import io.github.droidkaigi.confsched2020.data.api.response.AnnouncementListResponse
import io.github.droidkaigi.confsched2020.data.api.response.ContributorResponse
import io.github.droidkaigi.confsched2020.data.api.response.Response
import io.github.droidkaigi.confsched2020.data.api.response.SponsorListResponse
import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import kotlin.coroutines.CoroutineContext

internal open class KtorDroidKaigiApi constructor(
    private val httpClient: HttpClient,
    private val apiEndpoint: String,
    private val coroutineDispatcherForCallback: CoroutineContext?
) : DroidKaigiApi {
    private val json = Json(JsonConfiguration.Stable.copy(strictMode = false))
    override suspend fun getSessions(): Response {
        // We are separate getting response string and parsing for Kotlin Native
        val rawResponse = httpClient.get<String> {
            url("$apiEndpoint/timetable")
            accept(ContentType.Application.Json)
        }
        return json.parse(ResponseImpl.serializer(), rawResponse)
    }

    override fun getSessions(
        callback: (response: Response) -> Unit,
        onError: (error: Exception) -> Unit
    ) {
        GlobalScope.launch(requireNotNull(coroutineDispatcherForCallback)) {
            try {
                val response = getSessions()
                callback(response)
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    override fun getSessionsAsync(): Deferred<Response> =
        GlobalScope.async(requireNotNull(coroutineDispatcherForCallback)) {
            getSessions()
        }

    override fun getAnnouncementsAsync(lang: LangParameter): Deferred<AnnouncementListResponse> =
        GlobalScope.async(requireNotNull(coroutineDispatcherForCallback)) {
            getAnnouncements(lang)
        }

    override suspend fun getAnnouncements(lang: LangParameter): AnnouncementListResponse {
        val rawResponse = httpClient.get<String> {
            url("$apiEndpoint/announcements/${lang.value}")
            accept(ContentType.Application.Json)
        }

        return json.parse(AnnouncementResponseImpl.serializer().list, rawResponse)
    }

    override suspend fun getSponsors(): SponsorListResponse {
        val rawResponse = httpClient.get<String> {
            url("$apiEndpoint/sponsors")
            accept(ContentType.Application.Json)
        }

        return json.parse(SponsorResponseImpl.serializer().list, rawResponse)
    }

    override suspend fun getStaffs(): StaffResponse {
        val rawResponse = httpClient.get<String> {
            url("$apiEndpoint/committee_members")
            accept(ContentType.Application.Json)
        }

        return StaffResponseImpl(json.parse(StaffItemResponseImpl.serializer().list, rawResponse))
    }

    override suspend fun getContributorList(): ContributorResponse {
        val rawResponse = httpClient.get<String> {
            url("$apiEndpoint/contributors")
            accept(ContentType.Application.Json)
        }

        return ContributorResponseImpl(
            json.parse(
                ContributorItemResponseImpl.serializer().list,
                rawResponse
            )
        )
    }
}
