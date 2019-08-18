package io.github.droidkaigi.confsched2020.model

data class WifiConfiguration(
    val ssid: String,
    val password: String,
    val isRegistered: Boolean = false
)
