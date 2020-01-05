package io.github.droidkaigi.confsched2020.data.db.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.droidkaigi.confsched2020.data.db.entity.SpeakerEntity

@Entity(tableName = "speaker")
internal class SpeakerEntityImpl(
    @PrimaryKey override var id: String,
    @ColumnInfo(name = "speaker_name")
    override var name: String,
    @ColumnInfo(name = "speaker_tag_line")
    override var tagLine: String?,
    @ColumnInfo(name = "speaker_bio")
    override var bio: String?,
    @ColumnInfo(name = "speaker_image_url")
    override var imageUrl: String?
) : SpeakerEntity
