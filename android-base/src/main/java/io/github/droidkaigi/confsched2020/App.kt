package io.github.droidkaigi.confsched2020

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.createAppComponent

open class App : DaggerApplication() {
    val appComponent: AppComponent by lazy {
        createAppComponent()
    }

    override fun onCreate() {
        super.onCreate()
        setupFirestore()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }

    private fun setupFirestore() {
        FirebaseApp.initializeApp(this)
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .setPersistenceEnabled(true)
            .build()
        firestore.setFirestoreSettings(settings)
    }
}