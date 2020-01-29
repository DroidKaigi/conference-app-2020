package io.github.droidkaigi.confsched2020.preference.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.model.NightMode
import javax.inject.Inject

class PreferenceViewModel @Inject constructor() : ViewModel() {

    data class UiModel(
        val nightMode: NightMode
    ) {
        companion object {
            val EMPTY = UiModel(NightMode.SYSTEM)
        }
    }

    private val nightModeLiveData: MutableLiveData<NightMode> = MutableLiveData(NightMode.SYSTEM)

    var uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = nightModeLiveData
    ) { uiModel: UiModel, nightMode: NightMode ->
        UiModel(
            nightMode = nightMode
        )
    }

    fun setNightMode(newValue: NightMode) {
        nightModeLiveData.value = newValue
    }
}
