package io.github.droidkaigi.confsched2020.data.api.internal

import io.ktor.client.HttpClient
import javax.inject.Inject

internal class InjectableKtorGoogleFormApi @Inject constructor(
    httpClient: HttpClient
) : KtorGoogleFormApi(httpClient)
