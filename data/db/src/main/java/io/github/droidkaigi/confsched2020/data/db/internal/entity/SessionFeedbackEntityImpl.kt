package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.droidkaigi.confsched2020.data.db.entity.SessionFeedbackEntity

@Entity(tableName = "session_feedback")
internal data class SessionFeedbackEntityImpl(
    @PrimaryKey @ColumnInfo(name = "session_id", index = true)
    override var sessionId: String,
    @ColumnInfo(name = "total_evaluation") override val totalEvaluation: Int,
    override val relevancy: Int,
    override val asExpected: Int,
    override val difficulty: Int,
    override val knowledgeable: Int,
    override val comment: String,
    override val submitted: Boolean
) : SessionFeedbackEntity
