package io.github.droidkaigi.confsched2020.data.device

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.github.droidkaigi.confsched2020.data.device.internal.DeviceModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DeviceModule::class
    ]
)
interface DeviceComponent {
    fun WifiManager(): WifiManager

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): DeviceComponent
    }

    companion object {
        fun factory(): Factory = DaggerDeviceComponent.factory()
    }
}
