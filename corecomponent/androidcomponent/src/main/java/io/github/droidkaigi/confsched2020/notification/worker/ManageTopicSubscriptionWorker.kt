package io.github.droidkaigi.confsched2020.notification.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import io.github.droidkaigi.confsched2020.notification.Topic
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import timber.log.debug
import timber.log.error

class ManageTopicSubscriptionWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        try {
            if (!inputData.isValid) {
                return Result.failure()
            }

            val topicsToBeSubscribed =
                inputData.getStringArray(KEY_TOPIC_NAMES_TO_BE_SUBSCRIBED).orEmpty()

            val topicsToBeUnsubscribed =
                inputData.getStringArray(KEY_TOPIC_NAMES_TO_BE_UNSUBSCRIBED).orEmpty()

            FirebaseInstanceId.getInstance().instanceId.await()

            Timber.debug { "Found a token so proceed to subscribe the topic" }

            topicsToBeSubscribed.forEach { topicName ->
                FirebaseMessaging.getInstance().subscribeToTopic(topicName).await()

                Timber.debug { "Subscribed $topicName successfully" }
            }

            topicsToBeUnsubscribed.forEach { topicName ->
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName).await()

                Timber.debug { "Unsubscribed $topicName successfully" }
            }
        } catch (th: Throwable) {
            Timber.error(th) { "Fail ManageTopicSubscriptionWorker" }

            return Result.retry()
        }

        return Result.success()
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
