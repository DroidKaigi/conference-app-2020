package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import io.github.droidkaigi.confsched2020.ext.combine
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.StaffContents
import javax.inject.Inject

class StaffsViewModel @Inject constructor(
    store: Store<Unit, StaffContents>
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

    private val staffContentsLoadState: LiveData<StoreResponse<StaffContents>> =
        store.stream(StoreRequest.cached(key = Unit, refresh = true)).asLiveData()

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
}
