package io.github.droidkaigi.confsched2020.data.api.internal

import io.ktor.client.HttpClient
import javax.inject.Inject
import javax.inject.Named

internal class InjectableKtorDroidKaigiApi @Inject constructor(
    httpClient: HttpClient,
    @Named("apiEndpoint") apiEndpoint: String
) : KtorDroidKaigiApi(httpClient, apiEndpoint, null)
