package io.github.droidkaigi.confsched2020.session.ui.viewmodel

import com.jraska.livedata.test
import io.github.droidkaigi.confsched2020.model.AudienceCategory
import io.github.droidkaigi.confsched2020.model.Category
import io.github.droidkaigi.confsched2020.model.Filters
import io.github.droidkaigi.confsched2020.model.Lang
import io.github.droidkaigi.confsched2020.model.LangSupport
import io.github.droidkaigi.confsched2020.model.LocaledString
import io.github.droidkaigi.confsched2020.model.Room
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SessionList
import io.github.droidkaigi.confsched2020.model.SessionPage
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.session.util.SessionAlarm
import io.github.droidkaigi.confsched2020.widget.component.MockkRule
import io.github.droidkaigi.confsched2020.widget.component.ViewModelTestRule
import io.kotlintest.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SessionsViewModelTest {
    @get:Rule val viewModelTestRule = ViewModelTestRule()
    @get:Rule val mockkRule = MockkRule(this)
    @MockK(relaxed = true) lateinit var sessionRepository: SessionRepository
    @MockK lateinit var sessionAlarm: SessionAlarm

    @Test
    fun load() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        coEvery { sessionRepository.refresh() }
        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )

        val testObserver = sessionsViewModel
            .uiModel
            .test()
        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionsViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            dayToSessionsMap shouldBe mapOf(
                SessionPage.dayOfNumber(1) to SessionList(
                    listOf(
                        Dummies.serviceSession,
                        Dummies.speachSession1
                    )
                )
            )
            favoritedSessions shouldBe SessionList(listOf<Session>(
                Dummies.serviceSession
            ))
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

    @Test
    fun favorite() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)
        coEvery { sessionRepository.toggleFavorite(Dummies.speachSession1.id) } returns Unit

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )

        val testObserver = sessionsViewModel
            .uiModel
            .test()

        sessionsViewModel.favorite(Dummies.speachSession1)
        verify { sessionsViewModel.favorite(Dummies.speachSession1) }
        verify { sessionAlarm.toggleRegister(Dummies.speachSession1) }
        val valueHistory = testObserver.valueHistory()
        val dayToSessionsMap = mapOf(
            SessionPage.dayOfNumber(1) to listOf<Session>(
                Dummies.serviceSession,
                Dummies.speachSession1
            )
        )
        val filters = Filters()
        val allFilters = Filters(
            rooms = Dummies.sessionContents.rooms.toSet(),
            audienceCategories = Dummies.sessionContents.audienceCategories.toSet(),
            categories = Dummies.sessionContents.category.toSet(),
            langs = Dummies.sessionContents.langs.toSet(),
            langSupports = Dummies.sessionContents.langSupports.toSet()
        )
        valueHistory[0] shouldBe SessionsViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            favoritedSessions shouldBe SessionList(
                listOf<Session>(
                    Dummies.serviceSession
                )
            )
            dayToSessionsMap shouldBe dayToSessionsMap
            filters shouldBe filters
            allFilters shouldBe allFilters
        }
        valueHistory[2].apply {
            isLoading shouldBe true
            error shouldBe null
            favoritedSessions shouldBe SessionList(listOf<Session>(
                Dummies.serviceSession
            ))
            dayToSessionsMap shouldBe dayToSessionsMap
            filters shouldBe filters
            allFilters shouldBe allFilters
        }
        valueHistory[3].apply {
            isLoading shouldBe false
            error shouldBe null
            favoritedSessions shouldBe SessionList(listOf<Session>(
                Dummies.serviceSession
            ))
            dayToSessionsMap shouldBe dayToSessionsMap
            filters shouldBe filters
            allFilters shouldBe allFilters
        }
        valueHistory[4].apply {
            isLoading shouldBe true
            error shouldBe null
            favoritedSessions shouldBe SessionList(listOf<Session>(
                Dummies.serviceSession
            ))
            dayToSessionsMap shouldBe dayToSessionsMap
            filters shouldBe filters
            allFilters shouldBe allFilters
        }
        valueHistory[5].apply {
            isLoading shouldBe false
            error shouldBe null
            favoritedSessions shouldBe SessionList(listOf<Session>(
                Dummies.serviceSession
            ))
            dayToSessionsMap shouldBe dayToSessionsMap
            filters shouldBe filters
            allFilters shouldBe allFilters
        }
    }

    @Test
    fun filterChanged_room_true() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )

        val testObserver = sessionsViewModel
            .uiModel
            .test()
        val room1 = Room(1, LocaledString("JA App bar", "EN App bar"), 1)
        sessionsViewModel.filterChanged(room1, true)
        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionsViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters()
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(
                rooms = setOf(room1)
            )
        }
    }

    @Test
    fun filterChanged_room_false() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )
        // turn on once for testing.
        val room1 = Room(1, LocaledString("JA App bar", "EN App bar"), 1)
        val room2 = Room(2, LocaledString("JA App room", "EN App room"), 2)
        sessionsViewModel.filterChanged(room1, true)
        sessionsViewModel.filterChanged(room2, true)

        val testObserver = sessionsViewModel
            .uiModel
            .test()
        sessionsViewModel.filterChanged(room2, false)
        val valueHistory = testObserver.valueHistory()
        valueHistory[0].apply {
            isLoading shouldBe true
            error shouldBe null
            filters shouldBe Filters(rooms = setOf(room1, room2))
        }
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(rooms = setOf(room1, room2))
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(rooms = setOf(room1))
        }
    }

    @Test
    fun filterChanged_category_true() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )

        val testObserver = sessionsViewModel
            .uiModel
            .test()

        val category1 = Category(
            id = 0,
            name = LocaledString(ja = "category one ja", en = "category one en")
        )
        sessionsViewModel.filterChanged(category1, true)
        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionsViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters()
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(
                categories = setOf(category1)
            )
        }
    }

    @Test
    fun filterChanged_category_false() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )
        // turn on once for testing.
        val category1 = Category(
            id = 0,
            name = LocaledString(ja = "category one ja", en = "category one en")
        )
        val category2 = Category(
            id = 1,
            name = LocaledString(ja = "category two ja", en = "category two en")
        )
        sessionsViewModel.filterChanged(category1, true)
        sessionsViewModel.filterChanged(category2, true)

        val testObserver = sessionsViewModel
            .uiModel
            .test()
        sessionsViewModel.filterChanged(category2, false)
        val valueHistory = testObserver.valueHistory()
        valueHistory[0].apply {
            isLoading shouldBe true
            error shouldBe null
            filters shouldBe Filters(categories = setOf(category1, category2))
        }
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(categories = setOf(category1, category2))
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(categories = setOf(category1))
        }
    }

    @Test
    fun filterChanged_lang_true() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )

        val testObserver = sessionsViewModel
            .uiModel
            .test()

        sessionsViewModel.filterChanged(Lang.JA, true)
        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionsViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters()
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(
                langs = setOf(Lang.JA)
            )
        }
    }

    @Test
    fun filterChanged_lang_false() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )
        // turn on once for testing.
        sessionsViewModel.filterChanged(Lang.JA, true)
        sessionsViewModel.filterChanged(Lang.EN, true)

        val testObserver = sessionsViewModel
            .uiModel
            .test()
        sessionsViewModel.filterChanged(Lang.JA, false)
        val valueHistory = testObserver.valueHistory()
        valueHistory[0].apply {
            isLoading shouldBe true
            error shouldBe null
            filters shouldBe Filters(langs = setOf(Lang.JA, Lang.EN))
        }
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(langs = setOf(Lang.JA, Lang.EN))
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(
                langs = setOf(Lang.EN)
            )
        }
    }

    @Test
    fun filterChanged_langSupport_true() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )

        val testObserver = sessionsViewModel
            .uiModel
            .test()

        sessionsViewModel.filterChanged(LangSupport.INTERPRETATION, true)
        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionsViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters()
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(
                langSupports = setOf(LangSupport.INTERPRETATION)
            )
        }
    }

    @Test
    fun filterChanged_langSupport_false() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )
        // turn on once for testing.
        sessionsViewModel.filterChanged(LangSupport.INTERPRETATION, true)

        val testObserver = sessionsViewModel
            .uiModel
            .test()
        sessionsViewModel.filterChanged(LangSupport.INTERPRETATION, false)
        val valueHistory = testObserver.valueHistory()
        valueHistory[0].apply {
            isLoading shouldBe true
            error shouldBe null
            filters shouldBe Filters(langSupports = setOf(LangSupport.INTERPRETATION))
        }
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(langSupports = setOf(LangSupport.INTERPRETATION))
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(langSupports = setOf())
        }
    }

    @Test
    fun filterChanged_audienceCategory_true() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )

        val testObserver = sessionsViewModel
            .uiModel
            .test()

        sessionsViewModel.filterChanged(AudienceCategory.BEGINNERS, true)
        val valueHistory = testObserver.valueHistory()
        valueHistory[0] shouldBe SessionsViewModel.UiModel.EMPTY.copy(isLoading = true)
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters()
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(
                audienceCategories = setOf(AudienceCategory.BEGINNERS)
            )
        }
    }

    @Test
    fun filterChanged_audienceCategory_false() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )
        // turn on once for testing.
        sessionsViewModel.filterChanged(AudienceCategory.BEGINNERS, true)
        sessionsViewModel.filterChanged(AudienceCategory.UNSPECIFIED, true)

        val testObserver = sessionsViewModel
            .uiModel
            .test()
        sessionsViewModel.filterChanged(AudienceCategory.UNSPECIFIED, false)
        val valueHistory = testObserver.valueHistory()
        valueHistory[0].apply {
            isLoading shouldBe true
            error shouldBe null
            filters shouldBe Filters(
                audienceCategories = setOf(
                    AudienceCategory.BEGINNERS,
                    AudienceCategory.UNSPECIFIED
                )
            )
        }
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(
                audienceCategories = setOf(
                    AudienceCategory.BEGINNERS,
                    AudienceCategory.UNSPECIFIED
                )
            )
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(audienceCategories = setOf(AudienceCategory.BEGINNERS))
        }
    }

    @Test
    fun resetFilters() {
        coEvery { sessionRepository.sessionContents() } returns flowOf(Dummies.sessionContents)

        val sessionsViewModel = SessionsViewModel(
            sessionRepository = sessionRepository,
            sessionAlarm = sessionAlarm
        )
        sessionsViewModel.filterChanged(AudienceCategory.BEGINNERS, true)
        sessionsViewModel.filterChanged(LangSupport.INTERPRETATION, true)
        sessionsViewModel.filterChanged(Lang.JA, true)
        val category1 = Category(
            id = 0,
            name = LocaledString(ja = "category one ja", en = "category one en")
        )
        sessionsViewModel.filterChanged(category1, true)
        val room1 = Room(1, LocaledString("JA App bar", "EN App bar"), 1)
        sessionsViewModel.filterChanged(room1, true)

        val testObserver = sessionsViewModel
            .uiModel
            .test()

        sessionsViewModel.resetFilter()
        val valueHistory = testObserver.valueHistory()
        valueHistory[0].apply {
            isLoading shouldBe true
            error shouldBe null
            filters shouldBe Filters(
                rooms = setOf(room1),
                categories = setOf(category1),
                langs = setOf(Lang.JA),
                langSupports = setOf(LangSupport.INTERPRETATION),
                audienceCategories = setOf(AudienceCategory.BEGINNERS)
            )
        }
        valueHistory[1].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters(
                rooms = setOf(room1),
                categories = setOf(category1),
                langs = setOf(Lang.JA),
                langSupports = setOf(LangSupport.INTERPRETATION),
                audienceCategories = setOf(AudienceCategory.BEGINNERS)
            )
        }
        valueHistory[2].apply {
            isLoading shouldBe false
            error shouldBe null
            filters shouldBe Filters()
        }
    }
}
