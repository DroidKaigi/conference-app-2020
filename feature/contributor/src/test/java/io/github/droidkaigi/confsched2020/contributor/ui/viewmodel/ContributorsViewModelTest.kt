package io.github.droidkaigi.confsched2020.contributor.ui.viewmodel

import com.jraska.livedata.test
import io.github.droidkaigi.confsched2020.model.repository.ContributorRepository
import io.github.droidkaigi.confsched2020.widget.component.MockkRule
import io.github.droidkaigi.confsched2020.widget.component.ViewModelTestRule
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class ContributorsViewModelTest {
    @get:Rule
    val viewModelTestRule = ViewModelTestRule()
    @get:Rule
    val mockkRule = MockkRule(this)
    @MockK(relaxed = true)
    lateinit var contributorRepository: ContributorRepository

    @Test
    fun load() {
        coEvery { contributorRepository.contributorContents() } returns flowOf(Dummies.contributors)
        val contributorsViewModel = ContributorsViewModel(contributorRepository)

        val testObserver = contributorsViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe ContributorsViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe false
            contributors shouldBe Dummies.contributors
            error shouldBe null
        }
    }
}
