package io.github.droidkaigi.confsched2020.initializer

import android.app.Application

interface AppInitializer {
    fun initialize(application: Application)
}
