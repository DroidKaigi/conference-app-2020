package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.StaffContents
import io.github.droidkaigi.confsched2020.model.repository.StaffRepository
import javax.inject.Inject

class StaffsViewModel @Inject constructor(
    private val staffRepository: StaffRepository
) : ViewModel() {

    data class UiModel(
        val isLoading: Boolean,
        val error: AppError?,
        val staffContents: StaffContents
    ) {
        companion object {
            val EMPTY = UiModel(false, null, StaffContents.EMPTY)
        }
    }

    private val staffContentsLoadState: LiveData<LoadState<StaffContents>> = liveData {
        emitSource(
            staffRepository.staffs()
                .toLoadingState()
                .asLiveData()
        )
        staffRepository.refresh()
    }

    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = staffContentsLoadState
    ) { _, loadState ->
        val staffContents = when (loadState) {
            is LoadState.Loaded -> {
                loadState.value
            }
            else -> {
                StaffContents.EMPTY
            }
        }
        UiModel(
            isLoading = loadState.isLoading,
            error = loadState.getErrorIfExists().toAppError(),
            staffContents = staffContents
        )
    }
}
