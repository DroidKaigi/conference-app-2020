package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.shopify.livedataktx.filter
import com.shopify.livedataktx.map
import com.shopify.livedataktx.toKtx
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.StaffRepository
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.StaffContents
import kotlinx.coroutines.flow.collect

class StaffsViewModel @AssistedInject constructor(
    @Assisted private val state: SavedStateHandle,
    val staffRepository: StaffRepository
) : ViewModel() {

    val staffContentsLoadingState = liveData {
        staffRepository.refresh()
        staffRepository.staffs()
            .toLoadingState()
            .collect { loadingState: LoadingState<StaffContents> ->
                emit(loadingState)
            }
    }
    val staffContents =
        staffContentsLoadingState.toKtx()
            .filter { loadingState ->
                loadingState is LoadingState.Loaded
            }
            .map {
                it as LoadingState.Loaded
                it.value
            }

    @AssistedInject.Factory
    interface Factory {
        fun create(state: SavedStateHandle): StaffsViewModel
    }
}