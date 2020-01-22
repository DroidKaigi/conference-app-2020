package io.github.droidkaigi.confsched2020.image

import android.content.Context
import coil.Coil
import coil.ImageLoader
import coil.util.CoilUtils
import okhttp3.OkHttpClient

object CoilInitializer {
    fun init(context: Context) {
        Coil.setDefaultImageLoader {
            ImageLoader(context) {
                okHttpClient { buildHttpClient(context) }
            }
        }
    }

    private fun buildHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(CoilUtils.createDefaultCache(context))
            .addNetworkInterceptor(SessionizeVaryHeaderRemoveInterceptor())
            .build()
    }
}
