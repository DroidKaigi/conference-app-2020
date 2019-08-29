package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.shopify.livedataktx.filter
import com.shopify.livedataktx.map
import com.shopify.livedataktx.toKtx
import io.github.droidkaigi.confsched2020.data.repository.SessionRepository
import io.github.droidkaigi.confsched2020.ext.asLiveData
import io.github.droidkaigi.confsched2020.ext.toLoadingState
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionContents
import javax.inject.Inject

class SessionsViewModel @Inject constructor(
    val sessionRepository: SessionRepository
) : ViewModel() {

    val loadState: LiveData<LoadState<SessionContents>> = liveData {
        emitSource(
            sessionRepository.sessionContents()
                .toLoadingState()
                .asLiveData()
        )
        sessionRepository.refresh()
    }
    val sessionContents = loadState.toKtx()
        .filter { loadingState ->
            loadingState is LoadState.Loaded
        }
        .map {
            it as LoadState.Loaded
            it.value
        }

    fun favorite(session: Session): LiveData<Unit> {
        return liveData {
            sessionRepository.toggleFavorite(session)
        }
    }
}