package io.github.droidkaigi.confsched2020.initializer

import android.app.Application
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import io.github.droidkaigi.confsched2020.R
import javax.inject.Inject

class EmojiInitializer @Inject constructor() : AppInitializer {
    override fun initialize(application: Application) {
        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs
        )
        val config = FontRequestEmojiCompatConfig(application, fontRequest)
        EmojiCompat.init(config)
    }
}
