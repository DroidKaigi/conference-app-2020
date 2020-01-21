package io.github.droidkaigi.confsched2020.preference.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import io.github.droidkaigi.confsched2020.ext.combine
import javax.inject.Inject

class PreferenceViewModel @Inject constructor(): ViewModel() {

    data class UiModel(
        val isNightMode: Boolean
    ) {
        companion object {
            val EMPTY = UiModel(false)
        }
    }

    private val _nightModeLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    private val nightModeLiveData
        get() = _nightModeLiveData.distinctUntilChanged()

    var uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = nightModeLiveData
    ) { uiModel: UiModel, isNightMode: Boolean ->
        UiModel(
            isNightMode = isNightMode
        )
    }

    fun setNightMode(newValue: Boolean) {
        _nightModeLiveData.value = newValue

    }
}