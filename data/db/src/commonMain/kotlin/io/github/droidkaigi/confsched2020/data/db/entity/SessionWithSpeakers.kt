package io.github.droidkaigi.confsched2020.data.db.entity

interface SessionWithSpeakers {
    val session: SessionEntity
    val speakerIdList: List<String>
}
