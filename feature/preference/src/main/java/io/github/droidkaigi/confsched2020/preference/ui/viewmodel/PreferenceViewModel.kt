package io.github.droidkaigi.confsched2020.preference.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2020.ext.combine
import javax.inject.Inject

class PreferenceViewModel @Inject constructor() : ViewModel() {

    data class UiModel(
        val isNightMode: Boolean
    ) {
        companion object {
            val EMPTY = UiModel(false)
        }
    }

    private val nightModeLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    var uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = nightModeLiveData
    ) { uiModel: UiModel, isNightMode: Boolean ->
        UiModel(
            isNightMode = isNightMode
        )
    }

    fun setNightMode(newValue: Boolean) {
        nightModeLiveData.value = newValue
    }
}
