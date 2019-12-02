package io.github.droidkaigi.confsched2020.announcement.ui.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_AnnouncementAssistedInjectModule::class])
abstract class AnnouncementAssistedInjectModule
