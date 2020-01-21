package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import io.github.droidkaigi.confsched2020.data.db.entity.SessionSpeakerJoinEntity

@Entity(
    tableName = "session_speaker_join", primaryKeys = ["sessionId", "speakerId"],
    foreignKeys = [
        (
            ForeignKey(
                entity = SessionEntityImpl::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("sessionId"),
                onDelete = CASCADE
            )
            ),
        (
            ForeignKey(
                entity = SpeakerEntityImpl::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("speakerId"),
                onDelete = CASCADE
            )
            )
    ]
)
internal class SessionSpeakerJoinEntityImpl(
    override val sessionId: String,
    @ColumnInfo(index = true)
    override val speakerId: String
) : SessionSpeakerJoinEntity
