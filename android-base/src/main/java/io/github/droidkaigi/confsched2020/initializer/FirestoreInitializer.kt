package io.github.droidkaigi.confsched2020.initializer

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import javax.inject.Inject

class FirestoreInitializer @Inject constructor() : AppInitializer {
    override fun initialize(application: Application) {
        FirebaseApp.initializeApp(application)
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore.firestoreSettings = settings
    }
}
