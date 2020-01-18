package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.repository.StaffRepository
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.StaffContents
import javax.inject.Inject

class StaffsViewModel @Inject constructor(
    private val staffRepository: StaffRepository
) : ViewModel() {

    val staffContentsLoadState: LiveData<LoadState<StaffContents>> = liveData {
        emitSource(
            staffRepository.staffs()
                .toLoadingState()
                .asLiveData()
        )
        staffRepository.refresh()
    }
}
