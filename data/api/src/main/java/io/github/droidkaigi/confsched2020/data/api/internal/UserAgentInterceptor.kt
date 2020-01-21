package io.github.droidkaigi.confsched2020.data.api.internal

import io.github.droidkaigi.confsched2020.api.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

internal class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().apply {
                addHeader("User-Agent", "official-app-2020/${BuildConfig.VERSION_CODE} gzip")
            }.build()
        )
    }
}
