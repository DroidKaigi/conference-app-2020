package io.github.droidkaigi.confsched2020

import android.preference.PreferenceManager
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
import timber.log.LogcatTree
import timber.log.Timber

open class App : DaggerApplication(), AppComponentHolder {

    private val DARK_THEME_KEY = "darkTheme"

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
        val nightMode =
            when (PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .getString(DARK_THEME_KEY, getString(R.string.pref_theme_value_default))) {
                getString(R.string.pref_theme_value_dark) -> AppCompatDelegate.MODE_NIGHT_YES
                getString(R.string.pref_theme_value_light) -> AppCompatDelegate.MODE_NIGHT_NO
                getString(R.string.pref_theme_value_battery) -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private fun setupCoil() {
        CoilInitializer.init(this)
    }
}
