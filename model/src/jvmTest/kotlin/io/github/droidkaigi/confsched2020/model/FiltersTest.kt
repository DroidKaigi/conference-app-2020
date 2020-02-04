package io.github.droidkaigi.confsched2020.model

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.After
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Enclosed::class)
class FiltersTest {

    private companion object {
        val room1 = Room(10, LocaledString("部屋1", "room1"), 0)
        val room2 = Room(11, LocaledString("部屋2", "room2"), 0)
        val category1 = Category(10, LocaledString("ツール1", "Tool1"))
        val category2 = Category(11, LocaledString("ツール2", "Tool2"))
    }

    @RunWith(Parameterized::class)
    class WhenServiceSessionIsChecked(
        private val param: Param<ServiceSession>
    ) {
        companion object {
            private val serviceSession: ServiceSession = mockk()

            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun testParams() = listOf<Param<ServiceSession>>(
                Param(
                    title = "AFTER_PARTY is not filterable and passed",
                    sessionSetup = {
                        every { sessionType } returns SessionType.AFTER_PARTY
                    },
                    expected = true
                ),
                Param(
                    title = "LUNCH is not filterable and passed",
                    sessionSetup = {
                        every { sessionType } returns SessionType.LUNCH
                    },
                    expected = true
                ),
                Param(
                    title = "room1 filter passes CODELABS in room1",
                    filters = Filters(rooms = setOf(room1)),
                    sessionSetup = {
                        every { sessionType } returns SessionType.CODELABS
                        every { room } returns room1
                    },
                    expected = true
                ),
                Param(
                    title = "room1 filter does not pass CODELABS in room2",
                    filters = Filters(rooms = setOf(room1)),
                    sessionSetup = {
                        every { sessionType } returns SessionType.CODELABS
                        every { room } returns room2
                    },
                    expected = false
                )
            )
        }

        @After fun tearDown() {
            clearMocks(serviceSession)
        }

        @Test fun isPassForServiceSession(): Unit = with(param) {
            // setup
            sessionSetup(serviceSession)

            // verify
            assertEquals(expected = expected, actual = filters.isPass(serviceSession))
        }
    }

    @RunWith(Parameterized::class)
    class WhenSpeechSessionIsChecked(
        private val param: Param<SpeechSession>
    ) {
        companion object {
            private val speechSession: SpeechSession = mockk()

            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun params() = listOf(
                Param(
                    title = "empty filter passes empty session",
                    expected = true
                ),
                Param(
                    title = "room1 filter passes session in room1",
                    filters = Filters(rooms = setOf(room1)),
                    sessionSetup = {
                        every { room } returns room1
                    },
                    expected = true
                ),
                Param(
                    title = "room1 filter does not pass session in room2",
                    filters = Filters(rooms = setOf(room1)),
                    sessionSetup = {
                        every { room } returns room2
                    },
                    expected = false
                ),
                Param(
                    title = "category1 filter passes category1 session",
                    filters = Filters(categories = setOf(category1)),
                    sessionSetup = {
                        every { category } returns category1
                    },
                    expected = true
                ),
                Param(
                    title = "category2 filter does not pass category1 session",
                    filters = Filters(categories = setOf(category1)),
                    sessionSetup = {
                        every { category } returns category2
                    },
                    expected = false
                ),
                Param(
                    title = "JA filter passes Japanese session",
                    filters = Filters(langs = setOf(Lang.JA)),
                    sessionSetup = {
                        every { lang } returns Lang.JA
                    },
                    expected = true
                ),
                Param(
                    title = "JA filter does not pass English session",
                    filters = Filters(langs = setOf(Lang.JA)),
                    sessionSetup = {
                        every { lang } returns Lang.EN
                    },
                    expected = false
                ),
                Param(
                    title = "Interpretation filter passes interpretation session",
                    filters = Filters(langSupports = setOf(LangSupport.INTERPRETATION)),
                    sessionSetup = {
                        every { isInterpretationTarget } returns true
                    },
                    expected = true
                ),
                Param(
                    title = "Interpretation filter does not pass non interpretation session",
                    filters = Filters(langSupports = setOf(LangSupport.INTERPRETATION)),
                    sessionSetup = {
                        every { isInterpretationTarget } returns false
                    },
                    expected = false
                ),
                Param.forLevels(
                    title = "empty filter passes beginner session",
                    levelList = listOf(Level.BEGINNER),
                    expected = true
                ),
                Param.forLevels(
                    title = "empty filter passes intermediate session",
                    levelList = listOf(Level.INTERMEDIATE),
                    expected = true
                ),
                Param.forLevels(
                    title = "empty filter passes advanced session",
                    levelList = listOf(Level.ADVANCED),
                    expected = true
                ),
                Param.forLevels(
                    title = "Beginners filter passes beginners session",
                    filterItem = setOf(Level.BEGINNER),
                    levelList = listOf(Level.BEGINNER),
                    expected = true
                ),
                Param.forLevels(
                    title = "Beginners filter does not pass non beginners session",
                    filterItem = setOf(Level.BEGINNER),
                    levelList = listOf(Level.INTERMEDIATE),
                    expected = false
                ),
                Param.forLevels(
                    title = "filter has Beginners passes Beginners and Intermediate session",
                    filterItem = setOf(Level.BEGINNER),
                    levelList = listOf(Level.BEGINNER, Level.INTERMEDIATE),
                    expected = true
                ),
                Param.forLevels(
                    title = "filter has Advanced not passes Beginners and Intermediate session",
                    filterItem = setOf(Level.ADVANCED),
                    levelList = listOf(Level.BEGINNER, Level.INTERMEDIATE),
                    expected = false
                ),
                Param.forLevels(
                    title = "filter has Beginners and Intermediate passes beginners session",
                    filterItem = setOf(Level.BEGINNER, Level.INTERMEDIATE),
                    levelList = listOf(Level.BEGINNER),
                    expected = true
                ),
                Param.forLevels(
                    title = "filter has Beginners and Intermediate not passes Advanced session",
                    filterItem = setOf(Level.BEGINNER, Level.INTERMEDIATE),
                    levelList = listOf(Level.ADVANCED),
                    expected = false
                ),
                Param.forLevels(
                    title = "filter has Beginners and Intermediate" +
                        " passes Intermediate and Advanced session",
                    filterItem = setOf(Level.BEGINNER, Level.INTERMEDIATE),
                    levelList = listOf(Level.INTERMEDIATE, Level.ADVANCED),
                    expected = true
                )
            )
        }

        @After fun tearDown() {
            clearMocks(speechSession)
        }

        @Test fun isPass_forSpeechSession() = with(param) {
            // setup
            sessionSetup(speechSession)

            // verify
            assertEquals(expected = expected, actual = filters.isPass(speechSession))
        }
    }
}

data class Param<T>(
    val title: String,
    val filters: Filters = Filters(),
    val sessionSetup: T.() -> Unit = {},
    val expected: Boolean
) {
    override fun toString(): String = title

    companion object {
        fun forLevels(
            title: String,
            filterItem: Set<Level> = setOf(),
            levelList: List<Level>,
            expected: Boolean
        ) = Param<SpeechSession>(
            title = title,
            filters = Filters(levels = filterItem),
            sessionSetup = {
                every { levels } returns levelList
            },
            expected = expected
        )
    }
}
