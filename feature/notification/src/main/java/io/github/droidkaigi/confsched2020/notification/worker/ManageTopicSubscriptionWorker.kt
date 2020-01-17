package io.github.droidkaigi.confsched2020.notification.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import io.github.droidkaigi.confsched2020.notification.Topic
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ManageTopicSubscriptionWorker(
    context: Context,
    parameters: WorkerParameters
) : Worker(context, parameters) {
    override fun doWork(): ListenableWorker.Result {
        try {
            if (!inputData.isValid) {
                return ListenableWorker.Result.failure()
            }

            val topicsToBeSubscribed =
                inputData.getStringArray(KEY_TOPIC_NAMES_TO_BE_SUBSCRIBED).orEmpty()

            val topicsToBeUnsubscribed =
                inputData.getStringArray(KEY_TOPIC_NAMES_TO_BE_UNSUBSCRIBED).orEmpty()

            runBlocking {
                FirebaseInstanceId.getInstance().instanceId.await()

                topicsToBeSubscribed.forEach { topicName ->
                    FirebaseMessaging.getInstance().subscribeToTopic(topicName).await()
                }

                topicsToBeUnsubscribed.forEach { topicName ->
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName).await()

                }
            }
        } catch (th: Throwable) {
            return ListenableWorker.Result.retry()
        }

        return ListenableWorker.Result.success()
    }

    private val Data.isValid: Boolean
        get() =
            (getStringArray(KEY_TOPIC_NAMES_TO_BE_SUBSCRIBED)?.isNotEmpty() == true ||
                getStringArray(KEY_TOPIC_NAMES_TO_BE_UNSUBSCRIBED)?.isNotEmpty() == true)

    companion object {
        private const val NAME = "ManageTopicSubscriptionWorker"
        private const val KEY_TOPIC_NAMES_TO_BE_SUBSCRIBED = "KEY_TOPIC_NAMES_TO_BE_SUBSCRIBED"
        private const val KEY_TOPIC_NAMES_TO_BE_UNSUBSCRIBED = "KEY_TOPIC_NAMES_TO_BE_UNSUBSCRIBED"

        fun start(
            context: Context,
            subscribes: List<Topic> = emptyList(),
            unsubscribes: List<Topic> = emptyList()
        ) {
            WorkManager.getInstance(context)
                .beginUniqueWork(
                    NAME, ExistingWorkPolicy.APPEND,
                    OneTimeWorkRequestBuilder<ManageTopicSubscriptionWorker>()
                        .setConstraints(
                            Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                        )
                        .setInputData(
                            Data.Builder()
                                .putStringArray(
                                    KEY_TOPIC_NAMES_TO_BE_SUBSCRIBED,
                                    subscribes.map { it.name }.toTypedArray()
                                )
                                .putStringArray(
                                    KEY_TOPIC_NAMES_TO_BE_UNSUBSCRIBED,
                                    unsubscribes.map { it.name }.toTypedArray()
                                )
                                .build()
                        )
                        .build()
                )
                .enqueue()
        }
    }
}
