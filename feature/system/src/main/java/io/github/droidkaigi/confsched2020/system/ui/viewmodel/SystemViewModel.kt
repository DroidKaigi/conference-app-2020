package io.github.droidkaigi.confsched2020.system.ui.viewmodel

import android.app.Activity
import android.content.Intent
import android.provider.CalendarContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2020.ext.toNonNullSingleEvent
import io.github.droidkaigi.confsched2020.model.AppError
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
        } catch (e: Exception) {
            Timber.debug(e) { "Fail startActivity" }
        }
    }
}
