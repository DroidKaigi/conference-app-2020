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
import io.github.droidkaigi.confsched2020.ext.asLiveData
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.LoadingState
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionContents
import javax.inject.Inject

class SessionsViewModel @Inject constructor(
    val sessionRepository: SessionRepository
) : ViewModel() {

    val sessionContentsLoadingState: LiveData<LoadingState<SessionContents>> = liveData {
        emitSource(
            sessionRepository.sessionContents()
                .toLoadingState()
                .asLiveData()
        )
        sessionRepository.refresh()
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

    fun favorite(session: Session): LiveData<Unit> {
        return liveData {
            sessionRepository.toggleFavorite(session)
        }
    }
}