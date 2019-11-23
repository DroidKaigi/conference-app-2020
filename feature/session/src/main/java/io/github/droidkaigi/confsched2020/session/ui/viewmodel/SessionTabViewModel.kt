package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import io.github.droidkaigi.confsched2020.ext.requireValue
import javax.inject.Inject

class SessionTabViewModel @Inject constructor() : ViewModel() {
    data class UiModel(val expandedSession: Boolean)

    private val mutableExpandFilter = MutableLiveData(true)

    val uiModel = mutableExpandFilter.map {
        UiModel(it)
    }

    fun toggleExpand() {
        mutableExpandFilter.value = !mutableExpandFilter.requireValue()
    }
}