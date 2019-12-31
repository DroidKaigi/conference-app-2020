package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.work.WorkInfo
import com.jraska.livedata.test
import io.github.droidkaigi.confsched2019.widget.component.MockkRule
import io.github.droidkaigi.confsched2019.widget.component.ViewModelTestRule
import io.github.droidkaigi.confsched2020.data.repository.FavoriteToggleWorkerManager
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SessionDetailViewModelTest {
    @get:Rule val viewModelTestRule = ViewModelTestRule()
    @get:Rule val mockkRule = MockkRule(this)
    @MockK(relaxed = true) lateinit var favoriteToggleWorkerManager: FavoriteToggleWorkerManager

    @Test
    fun load() {
        every {
            favoriteToggleWorkerManager.liveData()
        } returns MutableLiveData(WorkInfo.State.SUCCEEDED)
        val sessionRepository = mockk<SessionRepository> {
            coEvery { sessionContents() } returns flowOf(SessionContents.EMPTY)
        }
        val sessionDetailViewModel = SessionDetailViewModel(
            sessionId = SessionId("1"),
            sessionRepository = sessionRepository,
            favoriteToggleWorkerManager = favoriteToggleWorkerManager
        )

        val testObserver = sessionDetailViewModel
            .uiModel
            .distinctUntilChanged()
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionDetailViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1] should { it.isLoading && it.session == null && it.error != null }
        valueHistory[2] should { !it.isLoading && it.session == null && it.error != null }
    }
}