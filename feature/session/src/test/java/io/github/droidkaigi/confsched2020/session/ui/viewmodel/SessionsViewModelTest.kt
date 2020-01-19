package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import com.jraska.livedata.test
import io.github.droidkaigi.confsched2020.model.Filters
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionPage
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.widget.component.MockkRule
import io.github.droidkaigi.confsched2020.widget.component.ViewModelTestRule
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SessionsViewModelTest {
    @get:Rule val viewModelTestRule = ViewModelTestRule()
    @get:Rule val mockkRule = MockkRule(this)
    @MockK(relaxed = true) lateinit var sessionRepository: SessionRepository

    @Test
    fun load(){
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        coEvery { sessionRepository.refresh()}
        val sessionsViewModel = SessionsViewModel(sessionRepository = sessionRepository)

        val testObserver = sessionsViewModel
            .uiModel
            .test()
        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionsViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            dayToSessionsMap shouldBe mapOf(
                SessionPage.dayOfNumber(1) to listOf<Session>(
                    Dummies.serviceSession,
                    Dummies.speachSession1
                )
            )
            favoritedSessions shouldBe listOf<Session>(
                Dummies.serviceSession
            )
            filters shouldBe Filters()
            allFilters shouldBe Filters(
                rooms = Dummies.sessionContents.rooms.toSet(),
                audienceCategories = Dummies.sessionContents.audienceCategories.toSet(),
                categories = Dummies.sessionContents.category.toSet(),
                langs = Dummies.sessionContents.langs.toSet(),
                langSupports = Dummies.sessionContents.langSupports.toSet()
            )
        }

    }
}