package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.data.db.entity.StaffEntity
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.Staff
import io.github.droidkaigi.confsched2020.model.StaffContents
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@FlowPreview
class StaffsViewModel @Inject constructor(
    private val api: DroidKaigiApi,
    private val staffDatabase: StaffDatabase
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

    private val store = StoreBuilder.fromNonFlow<String, StaffResponse> { api.getStaffs() }
        .persister(
            reader = { readFromLocal(staffDatabase) },
            writer = { _: String, output: StaffResponse -> staffDatabase.save(output) }
        )
        .cachePolicy(MemoryPolicy.builder().build())
        .build()

    private val staffContentsLoadState: LiveData<StoreResponse<StaffContents>> =
        store.stream(StoreRequest.cached(key = "", refresh = true)).asLiveData()

    val uiModel: LiveData<UiModel> = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = staffContentsLoadState
    ) { _, storeResponse ->
        val staffContents = when (storeResponse) {
            is StoreResponse.Data -> {
                storeResponse.value
            }
            else -> {
                StaffContents.EMPTY
            }
        }
        UiModel(
            isLoading = storeResponse is StoreResponse.Loading,
            error = storeResponse.errorOrNull().toAppError(),
            staffContents = staffContents
        )
    }

    private fun readFromLocal(staffDatabase: StaffDatabase): Flow<StaffContents> {
        return staffDatabase
            .staffs()
            .map { StaffContents(it.map { staffEntity -> staffEntity.toStaff() }) }
    }

    private fun StaffEntity.toStaff(): Staff = Staff(id, name, iconUrl, profileUrl)
}
