package io.github.droidkaigi.confsched2020.floormap.ui.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_FloorMapInjectModule::class])
abstract class FloorMapInjectModule
