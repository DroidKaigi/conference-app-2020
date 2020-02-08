package io.github.droidkaigi.confsched2020.notification.service

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.droidkaigi.confsched2020.notification.NotificationChannelInfo
import io.github.droidkaigi.confsched2020.notification.NotificationUtil
import io.github.droidkaigi.confsched2020.util.queryIntentAllActivities
import timber.log.Timber
import timber.log.debug
import timber.log.error

class MessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            Timber.debug {
                "remoteMessage:$remoteMessage"
            }
            when (val notification = remoteMessage.notification) {
                null -> {
                    Timber.debug { "Receive data message" }
                    handleDataNotification(remoteMessage, remoteMessage.data)
                }
                else -> {
                    Timber.debug { "Receive notification message" }
                    handleMessageNotification(remoteMessage, notification, remoteMessage.data)
                }
            }
        } catch (unexpected: Throwable) {
            Timber.error(unexpected) { "Notification fail" }
        }
    }

    private fun handleMessageNotification(
        remoteMessage: RemoteMessage,
        notification: RemoteMessage.Notification,
        data: Map<String, Any>
    ) {
        // drop other attributes for now
        val channelId = data[KEY_CHANNEL_ID] as? String ?: NotificationChannelInfo.DEFAULT.channelId
        val channel = NotificationChannelInfo.of(channelId)
        NotificationUtil.showNotification(
            this,
            notification.title ?: "",
            notification.body ?: "",
            getPendingIntent(notification, channel, data),
            channelId
        )
    }

    private fun handleDataNotification(
        remoteMessage: RemoteMessage,
        data: Map<String, Any>
    ) {
        // no-op
    }

    private fun getPendingIntent(
        notification: RemoteMessage.Notification,
        channel: NotificationChannelInfo,
        data: Map<String, Any>
    ): PendingIntent {
        Timber.debug { "getPendingIntent" }
        val options = bundleOf(
            *data.map { it.key to it.value }.toTypedArray()
        )

        val link = notification.link ?: Uri.parse(data["link"] as? String)
        if (link != null) {
            Timber.debug { "getPendingIntent link found" }
            val intent =
                Intent(Intent.ACTION_VIEW).setData(link)

            val possibleActivities = packageManager.queryIntentAllActivities(intent)
            if (possibleActivities.isNotEmpty()) {
                val droidKaigiResolveInfo =
                    possibleActivities.firstOrNull { it.activityInfo.packageName == packageName }
                if (droidKaigiResolveInfo != null) {
                    Timber.debug { "getPendingIntent link droidkaigi activity found" }
                    return PendingIntent.getActivity(
                        this, 0, intent.setPackage(packageName),
                        PendingIntent.FLAG_UPDATE_CURRENT, options
                    )
                }
                Timber.debug { "getPendingIntent link activity found" }
                return PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT, options
                )
            }
        }
        Timber.debug {
            "getPendingIntent link activity not found. " +
                "Choose url by notification channel ${channel.defaultLaunchUrl}"
        }

        val intent = Intent(Intent.ACTION_VIEW)
            .setData(Uri.parse(channel.defaultLaunchUrl))
            .setPackage(application.packageName)
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {

        // The server side relies on this value
        private const val KEY_CHANNEL_ID = "channel_id"
    }
}
