package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.droidkaigi.confsched2020.data.db.entity.SessionEntity

@Entity(tableName = "session")
internal data class SessionEntityImpl(
    @PrimaryKey override var id: String,
    override val isServiceSession: Boolean,
    override var enTitle: String,
    override var title: String,
    override var desc: String,
    override var stime: Long,
    override var etime: Long,
    override val sessionType: String?,
    override val intendedAudience: String?,
    override val videoUrl: String?,
    override val slideUrl: String?,
    override val isInterpretationTarget: Boolean,
    override var language: String,
    @Embedded override val category: CategoryEntityImpl?,
    @Embedded override val room: RoomEntityImpl?,
    @Embedded override val message: MessageEntityImpl?
) : SessionEntity
