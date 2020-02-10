package io.github.droidkaigi.confsched2020.system.ui.viewmodel

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.core.app.ShareCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2020.ext.toNonNullSingleEvent
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.AppError.ExternalIntegrationError.NoCalendarIntegrationFoundException
import timber.log.Timber
import timber.log.debug
import javax.inject.Inject

class SystemViewModel @Inject constructor() : ViewModel() {
    private val mutableErrorLiveData = MutableLiveData<AppError?>()
    val errorLiveData: LiveData<AppError> get() = mutableErrorLiveData.toNonNullSingleEvent()
    fun onError(error: AppError) {
        mutableErrorLiveData.value = error
    }

    fun sendEventToCalendar(
        activity: Activity,
        title: String,
        location: String,
        startDateTime: DateTime,
        endDateTime: DateTime
    ) {
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDateTime.unixMillisLong)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDateTime.unixMillisLong)
            .putExtra(CalendarContract.Events.TITLE, "[$location] $title")
            .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Timber.debug(e) { "Fail startActivity" }
            onError(NoCalendarIntegrationFoundException(e))
        }
    }

    fun shareURL(
        activity: Activity,
        url: String
    ) {
        try {
            ShareCompat.IntentBuilder.from(activity)
                .setText(url)
                .setType("text/plain")
                .startChooser()
        } catch (e: Exception) {
            Timber.debug(e) { "Fail startActivity" }
        }
    }

    fun navigateToAccessMap(activity: Activity) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:35.6219252,139.7190626?q=TOCビル")
        )
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        }
    }
}
