package io.github.droidkaigi.confsched2020.staff.ui.viewmodel

import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.StoreBuilder
import com.jraska.livedata.test
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.staff.ui.readFromLocal
import io.github.droidkaigi.confsched2020.widget.component.MockkRule
import io.github.droidkaigi.confsched2020.widget.component.ViewModelTestRule
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

@FlowPreview
class StaffsViewModelTest {
    @get:Rule
    val viewModelTest = ViewModelTestRule()
    @get:Rule
    val mockkRule = MockkRule(this)
    @MockK(relaxed = true)
    lateinit var droidKaigiApi: DroidKaigiApi
    @MockK(relaxed = true)
    lateinit var staffDatabase: StaffDatabase

    @Test
    fun load() {
        coEvery { droidKaigiApi.getStaffs() } returns Dummies.staffResponse
        coEvery { staffDatabase.save(any()) }
        coEvery { staffDatabase.staffs() } returns flowOf(listOf(Dummies.staffEntity))
        val store = StoreBuilder.fromNonFlow<Unit, StaffResponse> { droidKaigiApi.getStaffs() }
            .persister(
                reader = { readFromLocal(staffDatabase) },
                writer = { _: Unit, output: StaffResponse -> staffDatabase.save(output) }
            )
            .cachePolicy(MemoryPolicy.builder().build())
            .build()
        val staffsViewModel = StaffsViewModel(store)

        val testObserver = staffsViewModel
            .uiModel
            .test()

        val valueHistory = testObserver.valueHistory()
        // from cache
        valueHistory[0].apply {
            isLoading shouldBe false
            error shouldBe null
            staffContents shouldBe Dummies.staffContents
        }
        // then fetch from network
        valueHistory[1] shouldBe StaffsViewModel.UiModel.EMPTY.copy(isLoading = true)
    }
}
