package io.github.droidkaigi.confsched2020.session.broadcastreceiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.navigation.NavDeepLinkBuilder
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.notification.NotificationUtil
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentArgs
import io.github.droidkaigi.confsched2020.session.util.SessionAlarm.Companion.EXTRA_CHANNEL_ID
import io.github.droidkaigi.confsched2020.session.util.SessionAlarm.Companion.EXTRA_SESSION_ID
import io.github.droidkaigi.confsched2020.session.util.SessionAlarm.Companion.EXTRA_TEXT
import io.github.droidkaigi.confsched2020.session.util.SessionAlarm.Companion.EXTRA_TITLE
import io.github.droidkaigi.confsched2020.widget.component.R

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return
        val sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
        val title = intent.getStringExtra(EXTRA_TITLE)
        val text = intent.getStringExtra(EXTRA_TEXT)
        val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID)
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.navigation)
            .setDestination(R.id.session_detail)
            .setArguments(SessionDetailFragmentArgs(SessionId(sessionId), "").toBundle())
            .createTaskStackBuilder()
            .getPendingIntent(sessionId.hashCode(), PendingIntent.FLAG_UPDATE_CURRENT) ?: return
        NotificationUtil.showNotification(context, title, text, pendingIntent, channelId)
    }
}
