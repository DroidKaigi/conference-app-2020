package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.droidkaigi.confsched2020.data.db.entity.AnnouncementEntity

@Entity(tableName = "announcement")
internal class AnnouncementEntityImpl(
    @PrimaryKey override val id: Long,
    override val title: String,
    override val content: String,
    override val type: String,
    override val publishedAt: Long,
    override val lang: String
) : AnnouncementEntity
