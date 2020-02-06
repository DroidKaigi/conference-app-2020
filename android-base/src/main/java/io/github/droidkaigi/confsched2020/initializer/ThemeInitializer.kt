package io.github.droidkaigi.confsched2020.initializer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import io.github.droidkaigi.confsched2020.util.Prefs
import javax.inject.Inject

class ThemeInitializer @Inject constructor() : AppInitializer {
    override fun initialize(application: Application) {
        AppCompatDelegate.setDefaultNightMode(Prefs(application).getNightMode())
    }
}
