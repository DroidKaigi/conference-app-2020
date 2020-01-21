package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import io.github.droidkaigi.confsched2020.ext.requireValue
import io.github.droidkaigi.confsched2020.model.ExpandFilterState
import javax.inject.Inject

class SessionTabViewModel @Inject constructor() : ViewModel() {
    data class UiModel(val expandFilterState: ExpandFilterState)

    private val mutableExpandFilter = MutableLiveData(ExpandFilterState.EXPANDED)

    val uiModel = mutableExpandFilter.map {
        UiModel(it)
    }

    fun toggleExpand() {
        mutableExpandFilter.value = mutableExpandFilter.requireValue().toggledState()
    }

    fun setExpand(state: ExpandFilterState) {
        mutableExpandFilter.value = state
    }
}
