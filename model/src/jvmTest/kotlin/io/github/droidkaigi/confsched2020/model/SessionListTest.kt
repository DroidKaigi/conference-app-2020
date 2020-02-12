package io.github.droidkaigi.confsched2020.model

import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(Enclosed::class)
class SessionListTest {

    class dayToSessionMap {
        @Test
        fun should_group_session_by_day_number() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.dayNumber } returns 1
            every { sessionMock1.room.roomType } returns Room.RoomType.CARDS
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.dayNumber } returns 1
            every { sessionMock2.room.roomType } returns Room.RoomType.CARDS
            val sessionMock3 = mockk<Session>()
            every { sessionMock3.dayNumber } returns 2
            every { sessionMock3.room.roomType } returns Room.RoomType.CARDS

            val dayToSessionMap =
                SessionList(listOf(sessionMock1, sessionMock2, sessionMock3)).dayToSessionMap

            assertEquals(actual = dayToSessionMap.size, expected = 2)
            assertEquals(
                actual = dayToSessionMap[SessionPage.Day1],
                expected = SessionList(listOf(sessionMock1, sessionMock2))
            )
            assertEquals(
                actual = dayToSessionMap[SessionPage.Day2],
                expected = SessionList(listOf(sessionMock3))
            )
        }

        @Test
        fun should_filter_EXHIBITION_events() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.dayNumber } returns 1
            every { sessionMock1.room.roomType } returns Room.RoomType.EXHIBITION

            val dayToSessionMap = SessionList(listOf(sessionMock1)).dayToSessionMap

            assertTrue(dayToSessionMap.isEmpty())
        }

        @Test(expected = NoSuchElementException::class)
        fun should_throw_exception_if_dayNumber_is_not_Day1_nor_Day2() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.dayNumber } returns 100
            every { sessionMock1.room.roomType } returns Room.RoomType.CARDS

            SessionList(listOf(sessionMock1)).dayToSessionMap
        }
    }

    class events {
        @Test
        fun should_contain_only_EXHIBITION_type() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.room.roomType } returns Room.RoomType.EXHIBITION
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.room.roomType } returns Room.RoomType.CARDS
            val events = SessionList(listOf(sessionMock1, sessionMock2)).events

            assertEquals(actual = events.size, expected = 1)
            assertEquals(actual = events.getOrNull(0), expected = sessionMock1)
        }

        @Test
        fun should_be_empty_if_no_events_exist() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.room.roomType } returns Room.RoomType.CARDS
            val events = SessionList(listOf(sessionMock1)).events
            assertTrue(events.isEmpty())
        }
    }

    class favorited {
        @Test
        fun should_contain_only_favorited_session() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.isFavorited } returns true
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.isFavorited } returns false
            val favorited = SessionList(listOf(sessionMock1, sessionMock2)).favorited

            assertEquals(actual = favorited.size, expected = 1)
            assertEquals(actual = favorited.getOrNull(0), expected = sessionMock1)
        }

        @Test
        fun should_be_empty_if_no_favorited_event_exist() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.isFavorited } returns false
            val favorited = SessionList(listOf(sessionMock1)).favorited
            assertTrue(favorited.isEmpty())
        }
    }

    class currentSessionIndex {
        @Test
        fun should_get_minus_1_if_no_events_are_finished() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.isFinished } returns false
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.isFinished } returns false
            val currentSessionIndex =
                SessionList(listOf(sessionMock1, sessionMock2)).currentSessionIndex

            assertEquals(actual = currentSessionIndex, expected = -1)
        }

        @Test
        fun should_get_minus_1_if_all_events_are_finished() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.isFinished } returns true
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.isFinished } returns true
            val currentSessionIndex =
                SessionList(listOf(sessionMock1, sessionMock2)).currentSessionIndex

            assertEquals(actual = currentSessionIndex, expected = -1)
        }

        @Test
        fun should_get_last_finished_index_plus_1_if_some_events_are_finished() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.isFinished } returns true
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.isFinished } returns true
            val sessionMock3 = mockk<Session>()
            every { sessionMock3.isFinished } returns false
            val currentSessionIndex =
                SessionList(listOf(sessionMock1, sessionMock2, sessionMock3)).currentSessionIndex

            assertEquals(actual = currentSessionIndex, expected = 2)
        }
    }

    class toPageToScrollPositionMap {
        @Test
        fun should_contain_page_to_scroll_position_map() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.dayNumber } returns 1
            every { sessionMock1.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock1.isFinished } returns true
            every { sessionMock1.isFavorited } returns false
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.dayNumber } returns 1
            every { sessionMock2.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock2.isFinished } returns false
            every { sessionMock2.isFavorited } returns false
            val sessionMock3 = mockk<Session>()
            every { sessionMock3.dayNumber } returns 2
            every { sessionMock3.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock3.isFinished } returns false
            every { sessionMock3.isFavorited } returns false

            val map = SessionList(
                listOf(
                    sessionMock1,
                    sessionMock2,
                    sessionMock3
                )
            ).toPageToScrollPositionMap()

            assertEquals(actual = map.size, expected = 1)
            assertEquals(map[SessionPage.Day1], 1)
        }

        @Test
        fun should_contain_page_to_scroll_favorite_event_position_map() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.dayNumber } returns 1
            every { sessionMock1.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock1.isFinished } returns true
            every { sessionMock1.isFavorited } returns true
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.dayNumber } returns 1
            every { sessionMock2.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock2.isFinished } returns false
            every { sessionMock2.isFavorited } returns true
            val sessionMock3 = mockk<Session>()
            every { sessionMock3.dayNumber } returns 2
            every { sessionMock3.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock3.isFinished } returns false
            every { sessionMock3.isFavorited } returns true

            val map = SessionList(
                listOf(
                    sessionMock1,
                    sessionMock2,
                    sessionMock3
                )
            ).toPageToScrollPositionMap()

            assertEquals(actual = map.size, expected = 2)
            assertEquals(map[SessionPage.Day1], 1)
            assertEquals(map[SessionPage.Favorite], 1)
        }

        @Test
        fun should_be_empty_if_no_events_finished() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.dayNumber } returns 1
            every { sessionMock1.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock1.isFinished } returns false
            every { sessionMock1.isFavorited } returns true
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.dayNumber } returns 1
            every { sessionMock2.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock2.isFinished } returns false
            every { sessionMock2.isFavorited } returns true
            val sessionMock3 = mockk<Session>()
            every { sessionMock3.dayNumber } returns 2
            every { sessionMock3.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock3.isFinished } returns false
            every { sessionMock3.isFavorited } returns true

            val map = SessionList(
                listOf(
                    sessionMock1,
                    sessionMock2,
                    sessionMock3
                )
            ).toPageToScrollPositionMap()

            assertTrue(map.isEmpty())
        }
        @Test
        fun should_be_empty_if_all_events_finished() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.dayNumber } returns 1
            every { sessionMock1.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock1.isFinished } returns true
            every { sessionMock1.isFavorited } returns true
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.dayNumber } returns 1
            every { sessionMock2.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock2.isFinished } returns true
            every { sessionMock2.isFavorited } returns true
            val sessionMock3 = mockk<Session>()
            every { sessionMock3.dayNumber } returns 2
            every { sessionMock3.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock3.isFinished } returns true
            every { sessionMock3.isFavorited } returns true

            val map = SessionList(
                listOf(
                    sessionMock1,
                    sessionMock2,
                    sessionMock3
                )
            ).toPageToScrollPositionMap()

            assertTrue(map.isEmpty())
        }

        @Test
        fun should_contain_Day2_unfinished_favorite_session_if_Day1_finished() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.dayNumber } returns 1
            every { sessionMock1.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock1.isFinished } returns true
            every { sessionMock1.isFavorited } returns true
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.dayNumber } returns 1
            every { sessionMock2.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock2.isFinished } returns true
            every { sessionMock2.isFavorited } returns true
            val sessionMock3 = mockk<Session>()
            every { sessionMock3.dayNumber } returns 2
            every { sessionMock3.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock3.isFinished } returns false
            every { sessionMock3.isFavorited } returns true

            val map = SessionList(
                listOf(
                    sessionMock1,
                    sessionMock2,
                    sessionMock3
                )
            ).toPageToScrollPositionMap()

            assertEquals(actual = map.size, expected = 1)
            assertEquals(map[SessionPage.Favorite], 2)
        }

        @Test
        fun should_contain_Day2_and_Day2_unfinished_favorite_session_if_some_Day2_finished() {
            val sessionMock1 = mockk<Session>()
            every { sessionMock1.dayNumber } returns 1
            every { sessionMock1.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock1.isFinished } returns true
            every { sessionMock1.isFavorited } returns true
            val sessionMock2 = mockk<Session>()
            every { sessionMock2.dayNumber } returns 1
            every { sessionMock2.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock2.isFinished } returns true
            every { sessionMock2.isFavorited } returns true
            val sessionMock3 = mockk<Session>()
            every { sessionMock3.dayNumber } returns 2
            every { sessionMock3.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock3.isFinished } returns true
            every { sessionMock3.isFavorited } returns true
            val sessionMock4 = mockk<Session>()
            every { sessionMock4.dayNumber } returns 2
            every { sessionMock4.room.roomType } returns Room.RoomType.CARDS
            every { sessionMock4.isFinished } returns false
            every { sessionMock4.isFavorited } returns true

            val map = SessionList(
                listOf(
                    sessionMock1,
                    sessionMock2,
                    sessionMock3,
                    sessionMock4
                )
            ).toPageToScrollPositionMap()

            assertEquals(actual = map.size, expected = 2)
            assertEquals(map[SessionPage.Favorite], 3)
            assertEquals(map[SessionPage.Day2], 1)
        }
    }
}
