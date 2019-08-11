package io.github.droidkaigi.confsched2020.data.device

import io.github.droidkaigi.confsched2020.model.WifiConfiguration

interface WifiManager {
    class NotFoundException : IllegalStateException("could not find a WifiManager on this device")
    class CannotCreateException(override val message: String) : IllegalStateException(message)

    fun createWifiConfiguration(wifiConfiguration: WifiConfiguration)
}
