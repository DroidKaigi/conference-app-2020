package io.github.droidkaigi.confsched2020.image

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class SessionizeVaryHeaderRemoveInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (!isTargetResponse(response)) return response

        return response.newBuilder()
            .removeHeader("Vary")
            .build()
    }

    private fun isTargetResponse(response: Response): Boolean {
        return response.isSuccessful && isSessionizeImageResponse(response.request().url())
    }

    private fun isSessionizeImageResponse(url: HttpUrl): Boolean {
        return url.host() == "sessionize.com" && url.encodedPath() == "/image"
    }
}
