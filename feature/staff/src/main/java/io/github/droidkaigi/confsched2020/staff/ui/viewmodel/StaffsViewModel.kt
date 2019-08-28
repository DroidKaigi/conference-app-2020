package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.StaffRepository
import io.github.droidkaigi.confsched2020.ext.asLiveData
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.StaffContents
import javax.inject.Inject

class StaffsViewModel @Inject constructor(
    val staffRepository: StaffRepository
) : ViewModel() {

    val staffContentsLoadingState: LiveData<LoadingState<StaffContents>> = liveData {
        emitSource(
            staffRepository.staffs()
                .toLoadingState()
                .asLiveData()
        )
        staffRepository.refresh()
    }
}
