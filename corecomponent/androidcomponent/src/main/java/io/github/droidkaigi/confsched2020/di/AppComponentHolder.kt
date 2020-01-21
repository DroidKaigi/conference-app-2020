package io.github.droidkaigi.confsched2020.di

import io.github.droidkaigi.confsched2020.model.repository.SessionRepository

interface AppComponentHolder {
    val appComponent: AppComponentInterface
}

interface AppComponentInterface {
    fun sessionRepository(): SessionRepository
}
