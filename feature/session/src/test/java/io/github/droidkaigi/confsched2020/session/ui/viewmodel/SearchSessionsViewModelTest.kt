package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import com.jraska.livedata.test
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.widget.component.MockkRule
import io.github.droidkaigi.confsched2020.widget.component.ViewModelTestRule
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.junit.Rule

class SearchSessionsViewModelTest {
    @get:Rule val viewModelTestRule = ViewModelTestRule()
    @get:Rule val mockkRule = MockkRule(this)
    @MockK(relaxed = true) lateinit var sessionRepository: SessionRepository

    @Test
    fun load() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        val searchSessionViewModel = SearchSessionsViewModel(
            sessionRepository = sessionRepository
        )

        val testObserver = searchSessionViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()

        valueHistory[0] shouldBe SearchSessionsViewModel.UiModel.EMPTY
        valueHistory[1].apply {
            searchResult.sessions shouldBe Dummies.sessionContents.sessions
            searchResult.speakers shouldBe Dummies.sessionContents.speakers
            searchResult.query shouldBe ""
        }

    }

    @Test
    fun searchSession_notFound() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        val searchSessionViewModel = SearchSessionsViewModel(
            sessionRepository = sessionRepository
        )

        val testObserver = searchSessionViewModel
            .uiModel
            .test()

        searchSessionViewModel.updateSearchQuery("hoge")

        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SearchSessionsViewModel.UiModel.EMPTY
        valueHistory[1].apply {
            searchResult.sessions shouldBe Dummies.sessionContents.sessions
            searchResult.speakers shouldBe Dummies.sessionContents.speakers
            searchResult.query shouldBe ""
        }
        valueHistory[2].apply {
            searchResult.sessions shouldBe Dummies.sessionContents.sessions
            searchResult.speakers shouldBe Dummies.sessionContents.speakers
            searchResult.query shouldBe ""
        }
        valueHistory[3].apply {
            searchResult.sessions shouldBe listOf()
            searchResult.speakers shouldBe listOf()
            searchResult.query shouldBe "hoge"
        }

    }

    @Test
    fun searchSession_Found() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        val searchSessionViewModel = SearchSessionsViewModel(
            sessionRepository = sessionRepository
        )

        val testObserver = searchSessionViewModel
            .uiModel
            .test()

        searchSessionViewModel.updateSearchQuery("DroidKaigi")
        searchSessionViewModel.updateSearchQuery("droidKaigi")
        searchSessionViewModel.updateSearchQuery("Keynote")
        searchSessionViewModel.updateSearchQuery("speaker")

        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SearchSessionsViewModel.UiModel.EMPTY
        valueHistory[1].apply {
            searchResult.sessions shouldBe Dummies.sessionContents.sessions
            searchResult.speakers shouldBe Dummies.sessionContents.speakers
            searchResult.query shouldBe ""
        }
        valueHistory[2].apply {
            searchResult.sessions shouldBe Dummies.sessionContents.sessions
            searchResult.speakers shouldBe Dummies.sessionContents.speakers
            searchResult.query shouldBe ""
        }
        valueHistory[3].apply {
            searchResult.sessions shouldBe listOf(Dummies.speachSession1)
            searchResult.speakers shouldBe listOf()
            searchResult.query shouldBe "DroidKaigi"
        }
        valueHistory[4].apply {
            searchResult.sessions shouldBe listOf(Dummies.speachSession1)
            searchResult.speakers shouldBe listOf()
            searchResult.query shouldBe "droidKaigi"
        }
        valueHistory[5].apply {
            searchResult.sessions shouldBe listOf(Dummies.serviceSession)
            searchResult.speakers shouldBe listOf()
            searchResult.query shouldBe "Keynote"
        }
        valueHistory[6].apply {
            searchResult.sessions shouldBe listOf(Dummies.speachSession1)
            searchResult.speakers shouldBe Dummies.speakers
            searchResult.query shouldBe "speaker"
        }

    }
}