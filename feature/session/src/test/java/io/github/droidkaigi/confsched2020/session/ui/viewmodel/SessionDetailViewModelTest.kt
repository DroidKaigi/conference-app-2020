package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import com.jraska.livedata.test
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.widget.component.MockkRule
import io.github.droidkaigi.confsched2020.widget.component.ViewModelTestRule
import io.kotlintest.matchers.beOfType
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SessionDetailViewModelTest {
    @get:Rule val viewModelTestRule = ViewModelTestRule()
    @get:Rule val mockkRule = MockkRule(this)
    @MockK(relaxed = true)
    lateinit var sessionRepository: SessionRepository

    @Test
    fun load() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        val sessionDetailViewModel = SessionDetailViewModel(
            sessionId = Dummies.speachSession1.id,
            sessionRepository = sessionRepository,
            searchQuery = null
        )

        val testObserver = sessionDetailViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        // Observer does not receive UiModel.EMPTY,
        // because sessionRepository.sessionContents does not always emit
        // before UiModel is observed
        valueHistory[0].apply {
            isLoading shouldBe false
            session shouldBe Dummies.speachSession1
            showEllipsis shouldBe true
            searchQuery shouldBe null
        }
    }

    @Test
    fun load_NotFoundSession() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(SessionContents.EMPTY)
        val sessionDetailViewModel = SessionDetailViewModel(
            sessionId = SessionId("1"),
            sessionRepository = sessionRepository,
            searchQuery = null
        )

        val uiModelTestObserver = sessionDetailViewModel
            .uiModel
            .test()

        val uiModelValueHistory = uiModelTestObserver.valueHistory()
        uiModelValueHistory[0].apply {
            isLoading shouldBe false
            error should beOfType<AppError.ApiException.SessionNotFoundException>()
            session shouldBe null
            showEllipsis shouldBe true
            searchQuery shouldBe null
        }
    }

    @Test
    fun favorite() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        coEvery { sessionRepository.toggleFavorite(Dummies.speachSession1.id) } returns Unit
        val sessionDetailViewModel = SessionDetailViewModel(
            sessionId = Dummies.speachSession1.id,
            sessionRepository = sessionRepository,
            searchQuery = null
        )

        val testObserver = sessionDetailViewModel
            .uiModel
            .test()
        sessionDetailViewModel.favorite(Dummies.speachSession1)

        verify { sessionDetailViewModel.favorite(Dummies.speachSession1) }
        val valueHistory = testObserver.valueHistory()
        valueHistory[0].apply {
            isLoading shouldBe false
            error shouldBe null
            session shouldBe Dummies.speachSession1
            showEllipsis shouldBe true
            searchQuery shouldBe null
        }
        valueHistory[1].apply {
            isLoading shouldBe true
            error shouldBe null
            session shouldBe Dummies.speachSession1
            showEllipsis shouldBe true
            searchQuery shouldBe null
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            session shouldBe Dummies.speachSession1
            showEllipsis shouldBe true
            searchQuery shouldBe null
        }
    }

    @Test
    fun expandDescription() {
        val sessionDetailViewModel = SessionDetailViewModel(
            sessionId = Dummies.speachSession1.id,
            sessionRepository = sessionRepository,
            searchQuery = null
        )
        val testObserver = sessionDetailViewModel
            .uiModel
            .test()

        sessionDetailViewModel.expandDescription()
        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionDetailViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe true
            error shouldBe null
            session shouldBe null
            showEllipsis shouldBe false
            searchQuery shouldBe null
        }
    }

    @Test
    fun fromSearch() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        val sessionDetailViewModel = SessionDetailViewModel(
            sessionId = Dummies.speachSession1.id,
            sessionRepository = sessionRepository,
            searchQuery = "query"
        )

        val testObserver = sessionDetailViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0].apply {
            isLoading shouldBe false
            error shouldBe null
            session shouldBe Dummies.speachSession1
            showEllipsis shouldBe true
            searchQuery shouldBe "query"
        }
    }

    @Test
    fun thumbsUpCount() {
        coEvery {
            sessionRepository.thumbsUpCounts(Dummies.speachSession1.id)
        } returns flowOf(Dummies.thumbsUpCount.total)
        val sessionDetailViewModel = SessionDetailViewModel(
            sessionId = Dummies.speachSession1.id,
            sessionRepository = sessionRepository,
            searchQuery = null
        )
        val testObserver = sessionDetailViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionDetailViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe true
            error shouldBe null
            session shouldBe null
            showEllipsis shouldBe true
            searchQuery shouldBe null
            thumbsUpCount.total shouldBe Dummies.thumbsUpCount.total
        }
    }
}
