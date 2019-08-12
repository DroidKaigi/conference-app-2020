package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.Embedded
import androidx.room.Relation
import io.github.droidkaigi.confsched2020.data.db.entity.SessionWithSpeakers

internal data class SessionWithSpeakersImpl(
    @Embedded override val session: SessionEntityImpl
) : SessionWithSpeakers {
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId",
        projection = ["speakerId"],
        entity = SessionSpeakerJoinEntityImpl::class
    )
    override var speakerIdList: List<String> = emptyList()
}
