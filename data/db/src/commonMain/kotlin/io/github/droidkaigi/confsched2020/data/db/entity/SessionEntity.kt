package io.github.droidkaigi.confsched2020.data.db.entity

interface SessionEntity {
    var id: String
    var title: String
    var enTitle: String
    var desc: String
    var stime: Long
    var etime: Long
    val language: String
    val category: CategoryEntity?
    val intendedAudience: String?
    val videoUrl: String?
    val slideUrl: String?
    val isInterpretationTarget: Boolean
    val room: RoomEntity?
    val message: MessageEntity?
    val isServiceSession: Boolean
    val sessionType: String?
}
