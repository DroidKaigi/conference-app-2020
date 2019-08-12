package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import io.github.droidkaigi.confsched2020.data.db.entity.MessageEntity

internal data class MessageEntityImpl(
    @ColumnInfo(name = "message_ja")
    override var ja: String,
    @ColumnInfo(name = "message_en")
    override var en: String
) : MessageEntity
