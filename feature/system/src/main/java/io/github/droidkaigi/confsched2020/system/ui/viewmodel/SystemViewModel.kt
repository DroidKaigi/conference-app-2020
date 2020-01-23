package io.github.droidkaigi.confsched2020.system.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2020.ext.toNonNullSingleEvent
import io.github.droidkaigi.confsched2020.model.AppError
import javax.inject.Inject

class SystemViewModel @Inject constructor() : ViewModel() {
    private val mutableErrorLiveData = MutableLiveData<AppError?>()
    val errorLiveData: LiveData<AppError> get() = mutableErrorLiveData.toNonNullSingleEvent()
    fun onError(error: AppError) {
        mutableErrorLiveData.value = error
    }

    fun sendEventToGoogleCalendar(
        context: Context,
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
        context.startActivity(intent)
    }
}
