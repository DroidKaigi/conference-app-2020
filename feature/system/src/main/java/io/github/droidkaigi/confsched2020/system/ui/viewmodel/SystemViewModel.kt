package io.github.droidkaigi.confsched2020.system.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2020.ext.toNonNullSingleEvent
import io.github.droidkaigi.confsched2020.model.AppError
import javax.inject.Inject

class SystemViewModel @Inject constructor() : ViewModel() {
    private val mutableErrorLiveData = MutableLiveData<AppError?>()
    val errorLiveData: LiveData<AppError> get() = mutableErrorLiveData.toNonNullSingleEvent()
    fun onError(error: AppError) {
        mutableErrorLiveData.value = error
    }

    fun sendEventToCalendar(
        context: Context,
        title: String,
        location: String,
        startDateTime: DateTime,
        endDateTime: DateTime
    ) {
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDateTime.unixMillisLong)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDateTime.unixMillisLong)
            .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        context.startActivity(intent)
    }
}
