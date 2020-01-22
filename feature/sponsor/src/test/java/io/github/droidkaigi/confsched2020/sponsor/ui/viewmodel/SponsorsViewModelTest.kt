package io.github.droidkaigi.confsched2020.sponsor.ui.viewmodel

import com.jraska.livedata.test
import io.github.droidkaigi.confsched2020.model.repository.SponsorRepository
import io.github.droidkaigi.confsched2020.widget.component.MockkRule
import io.github.droidkaigi.confsched2020.widget.component.ViewModelTestRule
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SponsorsViewModelTest {
    @get:Rule val viewModelTestRule = ViewModelTestRule()
    @get:Rule val mockkRule = MockkRule(this)
    @MockK(relaxed = true) lateinit var sponsorRepository: SponsorRepository

    @Test
    fun load() {
        coEvery { sponsorRepository.sponsors() } returns flowOf(Dummies.sponsors)
        val sponsorsViewModel = SponsorsViewModel(sponsorRepository)

        val testObserver = sponsorsViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SponsorsViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe false
            sponsorCategories shouldBe Dummies.sponsors
            error shouldBe null
        }
    }
}
