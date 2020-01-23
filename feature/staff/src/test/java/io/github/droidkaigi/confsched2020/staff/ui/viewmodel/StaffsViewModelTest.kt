package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import com.jraska.livedata.test
import io.github.droidkaigi.confsched2020.model.repository.StaffRepository
import io.github.droidkaigi.confsched2020.widget.component.MockkRule
import io.github.droidkaigi.confsched2020.widget.component.ViewModelTestRule
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class StaffsViewModelTest {
    @get:Rule
    val viewModelTest = ViewModelTestRule()
    @get:Rule
    val mockkRule = MockkRule(this)
    @MockK(relaxed = true)
    lateinit var staffRepository: StaffRepository

    @Test
    fun load() {
        coEvery { staffRepository.staffs() } returns flowOf(Dummies.staffContents)
        val staffsViewModel = StaffsViewModel(staffRepository)

        val testObserver = staffsViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe StaffsViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            staffContents shouldBe Dummies.staffContents
        }
    }
}
