package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import com.jraska.livedata.test
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.model.SessionId
import io.github.droidkaigi.confsched2020.model.SpeakerId
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.widget.component.MockkRule
import io.github.droidkaigi.confsched2020.widget.component.ViewModelTestRule
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SpeakerViewModelTest {
    @get:Rule
    val viewModelTestRule = ViewModelTestRule()
    @get:Rule
    val mockkRule = MockkRule(this)
    @MockK(relaxed = true)
    lateinit var sessionRepository: SessionRepository

    @Test
    fun uiModel_correctly_loaded() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        val speakerViewModel = SpeakerViewModel(
            speakerId = Dummies.speakers.first().id,
            sessionRepository = sessionRepository
        )

        val testObserver = speakerViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0].isLoading shouldBe true // other properties are not deterministic.
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            speaker shouldBe Dummies.speakers.first()
            sessions shouldBe listOf(Dummies.speachSession1)
        }
    }

    @Test
    fun uiModel_correctly_loaded_which_has_multiple_sessions() {
        val speachSession2 = Dummies.speachSession1.copy(
            id = SessionId("speech_session_id_2")
        )

        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents.copy(
            sessions = listOf(
                Dummies.serviceSession,
                Dummies.speachSession1,
                speachSession2
            )
        ))
        val speakerViewModel = SpeakerViewModel(
            speakerId = Dummies.speakers.first().id,
            sessionRepository = sessionRepository
        )

        val testObserver = speakerViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0].isLoading shouldBe true // other properties are not deterministic.
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            speaker shouldBe Dummies.speakers.first()
            sessions shouldBe listOf(Dummies.speachSession1, speachSession2)
        }
    }

    @Test
    fun uiModel_notFoundSpeaker() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        val speakerViewModel = SpeakerViewModel(
            speakerId = SpeakerId("notExistId"),
            sessionRepository = sessionRepository
        )

        val testObserver = speakerViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0].isLoading shouldBe true // other properties are not deterministic.
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldNotBe null
            speaker shouldBe null
            sessions shouldBe listOf()
        }
    }

    @Test
    fun uiModel_notSessionContents() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(SessionContents.EMPTY)
        val speakerViewModel = SpeakerViewModel(
            speakerId = SpeakerId("anyId"),
            sessionRepository = sessionRepository
        )

        val testObserver = speakerViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0].isLoading shouldBe true // other properties are not deterministic.
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldNotBe null
            speaker shouldBe null
            sessions shouldBe listOf()
        }
    }
}