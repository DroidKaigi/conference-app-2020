package io.github.droidkaigi.confsched2020

import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.AppComponentHolder
import io.github.droidkaigi.confsched2020.di.createAppComponent
import io.github.droidkaigi.confsched2020.initializer.AppInitializers
import javax.inject.Inject

open class App : DaggerApplication(), AppComponentHolder {

    override val appComponent: AppComponent by lazy {
        createAppComponent()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }

    @Inject lateinit var initializers: AppInitializers

    override fun onCreate() {
        super.onCreate()
        initializers.initialize(this)
    }
}
