package io.github.droidkaigi.confsched2020.session.broadcastreceiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.navigation.NavDeepLinkBuilder
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentArgs
import io.github.droidkaigi.confsched2020.util.NotificationChannelInfo
import io.github.droidkaigi.confsched2020.util.NotificationUtil.showNotification
import io.github.droidkaigi.confsched2020.widget.component.R

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return
        val sessionId = intent.getParcelableExtra<SessionId>(EXTRA_SESSION_ID)
        val title = intent.getStringExtra(EXTRA_TITLE)
        val text = intent.getStringExtra(EXTRA_TEXT)
        val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID)
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.navigation)
            .setDestination(R.id.session_detail)
            .setArguments(SessionDetailFragmentArgs(sessionId).toBundle())
            .createTaskStackBuilder()
            .getPendingIntent(sessionId.hashCode(), PendingIntent.FLAG_UPDATE_CURRENT) ?: return
        showNotification(context, title, text, pendingIntent, channelId)
    }

    companion object {
        private const val EXTRA_SESSION_ID = "EXTRA_SESSION_ID"
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_TEXT = "EXTRA_TEXT"
        private const val EXTRA_CHANNEL_ID = "EXTRA_CHANNEL_ID"

        fun createIntent(
            context: Context,
            sessionId: SessionId,
            title: String,
            text: String,
            notificationChannelInfo: NotificationChannelInfo
        ): Intent {
            return Intent(context, NotificationBroadcastReceiver::class.java).apply {
                putExtra(EXTRA_SESSION_ID, sessionId)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_TEXT, text)
                putExtra(EXTRA_CHANNEL_ID, notificationChannelInfo.channelId)
            }
        }
    }
}
