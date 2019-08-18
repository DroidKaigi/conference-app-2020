package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.shopify.livedataktx.filter
import com.shopify.livedataktx.map
import com.shopify.livedataktx.toKtx
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2020.data.repository.SessionRepository
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionContents
import kotlinx.coroutines.flow.collect

class SessionsViewModel @AssistedInject constructor(
    @Assisted private val state: SavedStateHandle,
    val sessionRepository: SessionRepository
) : ViewModel() {

    val sessionContentsLoadingState = liveData {
        sessionRepository.refresh()
        sessionRepository.sessionContents()
            .toLoadingState()
            .collect { loadingState: LoadingState<SessionContents> ->
                emit(loadingState)
            }
    }
    val sessionContents =
        sessionContentsLoadingState.toKtx()
            .filter { loadingState ->
                loadingState is LoadingState.Loaded
            }
            .map {
                it as LoadingState.Loaded
                it.value
            }

    fun favorite(session:Session): LiveData<Unit> {
        return liveData {
            sessionRepository.toggleFavorite(session)
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(state: SavedStateHandle): SessionsViewModel
    }
}