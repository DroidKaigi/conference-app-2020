package io.github.droidkaigi.confsched2020.initializer

import android.app.Application
import timber.log.LogcatTree
import timber.log.Timber
import javax.inject.Inject

class TimberInitializer @Inject constructor() : AppInitializer {
    override fun initialize(application: Application) {
        Timber.plant(LogcatTree())
    }
}
