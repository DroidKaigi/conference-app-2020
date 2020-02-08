package io.github.droidkaigi.confsched2020.initializer

import android.app.Application
import io.github.droidkaigi.confsched2020.model.Lang
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.notification.Topic
import io.github.droidkaigi.confsched2020.notification.worker.ManageTopicSubscriptionWorker
import javax.inject.Inject

class FirebaseMessagingInitializer @Inject constructor() : AppInitializer {
    override fun initialize(application: Application) {
        val allAnnouncementTopics = arrayOf(Topic.JaAnnouncement, Topic.EnAnnouncement)

        val subscribeTopic = when (defaultLang()) {
            Lang.EN -> Topic.EnAnnouncement
            Lang.JA -> Topic.JaAnnouncement
        }

        ManageTopicSubscriptionWorker.start(
            application,
            subscribes = listOf(subscribeTopic),
            unsubscribes = allAnnouncementTopics.filter { it != subscribeTopic }
        )
    }
}
