package io.github.droidkaigi.confsched2020.system.ui.viewmodel

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.ext.toNonNullSingleEvent
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.system.R
import javax.inject.Inject

class SystemViewModel @Inject constructor(
    val activity: FragmentActivity
) : ViewModel() {
    private val mutableErrorLiveData = MutableLiveData<AppError?>()
    val errorLiveData: LiveData<AppError> get() = mutableErrorLiveData.toNonNullSingleEvent()
    fun onError(error: AppError) {
        mutableErrorLiveData.value = error
    }

    fun openUrl(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .enableUrlBarHiding()
            .setToolbarColor(activity.getThemeColor(R.attr.colorAccent))
            .build()

        // block to multiple launch a Activity
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // launch a Custom Tabs Activity
        customTabsIntent.launchUrl(activity, Uri.parse(url))
    }


    fun sendEventToGoogleCalendar(
        title: String,
        location: String,
        startUnixMillis: Long,
        endUnixMillis: Long
    ) {
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startUnixMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endUnixMillis)
            .putExtra(CalendarContract.Events.TITLE, "DroidKaigi2020: $title")
            .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        activity.startActivity(intent)
    }
}
