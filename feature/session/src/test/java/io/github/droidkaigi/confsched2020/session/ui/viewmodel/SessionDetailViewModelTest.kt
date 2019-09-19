package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import androidx.lifecycle.distinctUntilChanged
import com.jraska.livedata.test
import io.github.droidkaigi.confsched2019.widget.component.ViewModelTestRule
import io.github.droidkaigi.confsched2020.data.repository.SessionRepository
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionId
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SessionDetailViewModelTest {
    @get:Rule val viewModelTestRule = ViewModelTestRule()

    @Test
    fun load() {
        val sessionRepository = mockk<SessionRepository> {
            coEvery { sessionContents() } returns flowOf(SessionContents.EMPTY)
        }
        val sessionDetailViewModel = SessionDetailViewModel(SessionId("1"), sessionRepository)

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