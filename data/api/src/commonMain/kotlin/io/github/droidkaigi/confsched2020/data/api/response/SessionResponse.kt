package io.github.droidkaigi.confsched2020.data.api.response

interface SessionResponse {
    val id: String
    val isServiceSession: Boolean
    val title: LocaledResponse?
    val speakers: List<String>
    val description: String?
    val startsAt: String?
    val endsAt: String?
    val language: String?
    val roomId: Int?
    val sessionCategoryItemId: Int?
    val message: SessionMessageResponse?
    val isPlenumSession: Boolean
    val sessionType: String?
    val targetAudience: String
    val videoUrl: String?
    val slideUrl: String?
    val interpretationTarget: Boolean
}
