package io.github.droidkaigi.confsched2020.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.droidkaigi.confsched2020.widget.component.R

object NotificationUtil {

    fun showNotification(
        context: Context,
        title: String,
        text: String,
        pendingIntent: PendingIntent,
        channelId: String
    ) {
        val notificationBuilder = notificationBuilder(context, channelId).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                showBundleNotification(context, title, channelId)
                setGroup(channelId)
            } else {
                setContentTitle(title)
            }
            setContentText(text)
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )
            setContentIntent(pendingIntent)
            setAutoCancel(true)
            // FIXME: Please replace with transparent icon
            setSmallIcon(R.mipmap.notification_icon)
        }

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(text.hashCode(), notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showBundleNotification(context: Context, title: String, channelId: String) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val notification = notificationBuilder(context, channelId)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setSummaryText(title)
            )
            // FIXME: Please replace with transparent icon
            .setSmallIcon(R.mipmap.notification_icon)
            .setGroup(channelId)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(channelId.hashCode(), notification)
    }

    private fun notificationBuilder(
        context: Context,
        channelId: String
    ): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createDefaultNotificationChannel(context, NotificationChannelInfo.of(channelId))
        }
        val builder = NotificationCompat.Builder(context, channelId)
        builder.setChannelId(channelId)
        return builder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDefaultNotificationChannel(
        context: Context,
        notificationChannelInfo: NotificationChannelInfo
    ) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            notificationChannelInfo.channelId,
            notificationChannelInfo.channelName(context),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }
}

enum class NotificationChannelInfo(
    val channelId: String,
    private val channelNameResId: Int
) {
    DEFAULT(
        "default_channel",
        R.string.app_name
    ),
    FAVORITE_SESSION_START(
        "favorite_session_start_channel",
        R.string.notification_channel_name_start_favorite_session
    );

    fun channelName(context: Context): String = context.getString(channelNameResId)

    companion object {
        fun of(channelId: String): NotificationChannelInfo {
            return values().find {
                it.channelId == channelId
            } ?: DEFAULT
        }
    }
}
