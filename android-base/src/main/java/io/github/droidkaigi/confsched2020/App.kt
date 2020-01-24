package io.github.droidkaigi.confsched2020

import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.AppComponentHolder
import io.github.droidkaigi.confsched2020.di.createAppComponent
import io.github.droidkaigi.confsched2020.image.CoilInitializer
import io.github.droidkaigi.confsched2020.util.Prefs
import timber.log.LogcatTree
import timber.log.Timber

open class App : DaggerApplication(), AppComponentHolder {

    override val appComponent: AppComponent by lazy {
        createAppComponent()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupFirestore()
        setupNightMode()
        setupCoil()
    }

    private fun setupTimber() {
        Timber.plant(LogcatTree())
    }

    private fun setupFirestore() {
        FirebaseApp.initializeApp(this)
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore.firestoreSettings = settings
    }

    private fun setupNightMode() {
        AppCompatDelegate.setDefaultNightMode(Prefs(this).getNightMode())
    }

    private fun setupCoil() {
        CoilInitializer.init(this)
    }
}
