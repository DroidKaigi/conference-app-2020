package io.github.droidkaigi.confsched2020.data.api

import io.github.droidkaigi.confsched2020.data.api.internal.KtorDroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.internal.KtorGoogleFormApi
import io.github.droidkaigi.confsched2020.data.api.internal.apiEndpoint
import io.ktor.client.HttpClient
import io.ktor.client.engine.ios.Ios
import io.ktor.client.features.UserAgent
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.serialization.json.Json
import platform.Foundation.NSBundle

// TODO: Replace with DI tools.
internal fun generateHttpClient(): HttpClient {
    val version =
        NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as String
    return HttpClient(Ios) {
        install(UserAgent) {
            agent = "official-app-2020/$version gzip"
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json.nonstrict)
        }
    }
}

fun generateDroidKaigiApi(): DroidKaigiApi {
    return KtorDroidKaigiApi(
        generateHttpClient(),
        apiEndpoint(),
        MainLoopDispatcher + CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }
    )
}

fun generateGoogleFormApi(): GoogleFormApi {
    return KtorGoogleFormApi(generateHttpClient())
}
