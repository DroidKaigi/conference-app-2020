package io.github.droidkaigi.confsched2020.system.ui.viewmodel

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
}
