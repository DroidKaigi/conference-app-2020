package io.github.droidkaigi.confsched2020.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import io.github.droidkaigi.confsched2020.initializer.AppInitializer
import io.github.droidkaigi.confsched2020.initializer.AppInjector
import io.github.droidkaigi.confsched2020.initializer.CoilInitializer
import io.github.droidkaigi.confsched2020.initializer.EmojiInitializer
import io.github.droidkaigi.confsched2020.initializer.FirebaseMessagingInitializer
import io.github.droidkaigi.confsched2020.initializer.FirestoreInitializer
import io.github.droidkaigi.confsched2020.initializer.ThemeInitializer
import io.github.droidkaigi.confsched2020.initializer.TimberInitializer

@Module
abstract class AppModuleBinds {
    @Binds
    @IntoSet
    abstract fun provideAppInjector(bind: AppInjector): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideCoilInitializer(bind: CoilInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideEmojiInitializer(bind: EmojiInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideFirestoreInitializer(bind: FirestoreInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideFirebaseMessagingInitializer(
        bind: FirebaseMessagingInitializer
    ): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideThemeInitializer(bind: ThemeInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideTimberInitializer(bind: TimberInitializer): AppInitializer
}
