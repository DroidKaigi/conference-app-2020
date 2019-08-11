package io.github.droidkaigi.confsched2020.model

import java.util.Locale

actual fun defaultLang() = if (Locale.getDefault() == Locale.JAPAN) Lang.JA else Lang.EN
