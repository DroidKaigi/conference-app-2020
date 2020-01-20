package io.github.droidkaigi.confsched2020.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.data.device.DeviceComponent
import io.github.droidkaigi.confsched2020.data.device.WifiManager
import javax.inject.Singleton

@Module
object DeviceComponentModule {
    @Provides @Singleton
    fun provideWifiManager(application: Application): WifiManager {
        return DeviceComponent.factory()
            .create(application)
            .WifiManager()
    }
}
