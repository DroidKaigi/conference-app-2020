package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.StoreBuilder
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.data.db.entity.StaffEntity
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.Staff
import io.github.droidkaigi.confsched2020.model.StaffContents
import io.github.droidkaigi.confsched2020.model.repository.StaffRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@FlowPreview
class StaffsViewModel @Inject constructor(
    private val api: DroidKaigiApi,
    private val staffDatabase: StaffDatabase,
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

    private val store = StoreBuilder.fromNonFlow<Unit, StaffResponse> { api.getStaffs() }
        .persister(
            reader = { readFromLocal() },
            writer = { _: Unit, output: StaffResponse -> staffDatabase.save(output) }
        )
        .cachePolicy(MemoryPolicy.builder().build())
        .build()

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

    private fun readFromLocal() = staffDatabase
        .staffs()
        .map { StaffContents(it.map { staffEntity -> staffEntity.toStaff() }) }

    private fun StaffEntity.toStaff(): Staff = Staff(id, name, iconUrl, profileUrl)

}
