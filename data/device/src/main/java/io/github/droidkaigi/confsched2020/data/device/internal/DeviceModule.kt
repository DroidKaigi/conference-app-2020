package io.github.droidkaigi.confsched2020.data.device.internal

import dagger.Binds
import dagger.Module
import io.github.droidkaigi.confsched2020.data.device.WifiManager

@Module(includes = [DeviceModule.Providers::class])
internal abstract class DeviceModule {
    @Binds abstract fun WifiManager(impl: AndroidWifiManager): WifiManager

    @Module
    internal object Providers
}
