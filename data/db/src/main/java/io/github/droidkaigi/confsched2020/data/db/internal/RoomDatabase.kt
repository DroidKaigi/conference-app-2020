package io.github.droidkaigi.confsched2020.data.db.internal

import androidx.room.withTransaction
import io.github.droidkaigi.confsched2020.data.api.response.AnnouncementResponse
import io.github.droidkaigi.confsched2020.data.api.response.ContributorResponse
import io.github.droidkaigi.confsched2020.data.api.response.Response
import io.github.droidkaigi.confsched2020.data.api.response.SponsorListResponse
import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import io.github.droidkaigi.confsched2020.data.db.AnnouncementDatabase
import io.github.droidkaigi.confsched2020.data.db.ContributorDatabase
import io.github.droidkaigi.confsched2020.data.db.SessionDatabase
import io.github.droidkaigi.confsched2020.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.data.db.entity.AnnouncementEntity
import io.github.droidkaigi.confsched2020.data.db.entity.ContributorEntity
import io.github.droidkaigi.confsched2020.data.db.entity.SessionFeedbackEntity
import io.github.droidkaigi.confsched2020.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2020.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2020.data.db.entity.SponsorEntity
import io.github.droidkaigi.confsched2020.data.db.entity.StaffEntity
import io.github.droidkaigi.confsched2020.data.db.internal.dao.AnnouncementDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.ContributorDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SessionDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SessionFeedbackDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SessionSpeakerJoinDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SpeakerDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.SponsorDao
import io.github.droidkaigi.confsched2020.data.db.internal.dao.StaffDao
import io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper.toAnnouncementEntities
import io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper.toContributorEntities
import io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper.toSessionEntities
import io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper.toSessionFeedbackEntity
import io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper.toSessionSpeakerJoinEntities
import io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper.toSpeakerEntities
import io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper.toSponsorEntities
import io.github.droidkaigi.confsched2020.data.db.internal.entity.mapper.toStaffEntities
import io.github.droidkaigi.confsched2020.model.SessionFeedback
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class RoomDatabase @Inject constructor(
    private val cacheDatabase: CacheDatabase,
    private val sessionDao: SessionDao,
    private val speakerDao: SpeakerDao,
    private val sessionSpeakerJoinDao: SessionSpeakerJoinDao,
    private val sessionFeedbackDao: SessionFeedbackDao,
    private val sponsorDao: SponsorDao,
    private val announcementDao: AnnouncementDao,
    private val staffDao: StaffDao,
    private val contributorDao: ContributorDao
) : SessionDatabase,
    SponsorDatabase,
    AnnouncementDatabase,
    StaffDatabase,
    ContributorDatabase {
    override fun sessions(): Flow<List<SessionWithSpeakers>> {
        return sessionSpeakerJoinDao.getAllSessionsFlow()
    }

    override fun allSpeaker(): Flow<List<SpeakerEntity>> = speakerDao.getAllSpeakerFlow()

    override suspend fun save(apiResponse: Response) {
        cacheDatabase.withTransaction {
            // FIXME: SQLiteDatabaseLockedException
            cacheDatabase.sqlite().execSQL("PRAGMA defer_foreign_keys = TRUE")
            sessionSpeakerJoinDao.deleteAll()
            speakerDao.deleteAll()
            sessionDao.deleteAll()
            val speakers = apiResponse.speakers.orEmpty().toSpeakerEntities()
            speakerDao.insert(speakers)
            val sessions = apiResponse.sessions
            val sessionEntities = sessions.toSessionEntities(
                apiResponse.categories.orEmpty(),
                apiResponse.rooms.orEmpty()
            )
            sessionDao.insert(sessionEntities)
            sessionSpeakerJoinDao.insert(sessions.toSessionSpeakerJoinEntities())
        }
    }

    override suspend fun sessionFeedbacks(): List<SessionFeedbackEntity> {
        return sessionFeedbackDao.sessionFeedbacks()
    }

    override suspend fun saveSessionFeedback(sessionFeedback: SessionFeedback) {
        sessionFeedbackDao.upsert(sessionFeedback.toSessionFeedbackEntity())
    }

    override fun sponsors(): Flow<List<SponsorEntity>> {
        return sponsorDao.allSponsors()
    }

    override suspend fun saveSponsors(apiResponse: SponsorListResponse) {
        cacheDatabase.withTransaction {
            val sponsors = apiResponse.toSponsorEntities()
            sponsorDao.deleteAll()
            sponsorDao.insert(sponsors)
        }
    }

    override fun announcementsByLang(lang: String): Flow<List<AnnouncementEntity>> =
        announcementDao.announcementsByLang(lang)

    override suspend fun save(apiResponse: List<AnnouncementResponse>) {
        cacheDatabase.withTransaction {
            val announcements = apiResponse.toAnnouncementEntities()
            announcementDao.deleteAll()
            announcementDao.insert(announcements)
        }
    }

    override fun staffs(): Flow<List<StaffEntity>> = staffDao.allStaffs()

    override suspend fun save(apiResponse: StaffResponse) {
        cacheDatabase.withTransaction {
            val staffs = apiResponse.staffs.toStaffEntities()
            staffDao.deleteAll()
            staffDao.insert(staffs)
        }
    }

    override fun contributorList(): Flow<List<ContributorEntity>> =
        contributorDao.allContributors()

    override suspend fun save(apiResponse: ContributorResponse) {
        cacheDatabase.withTransaction {
            val contributors = apiResponse.contributors.toContributorEntities()
            contributorDao.deleteAll()
            contributorDao.insert(contributors)
        }
    }
}
