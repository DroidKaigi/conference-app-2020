package io.github.droidkaigi.confsched2020.data.api.response

interface SpeakerResponse {
    val firstName: String?
    val lastName: String?
    val profilePicture: String?
    val sessions: List<Int?>?
    val tagLine: String?
    val isTopSpeaker: Boolean?
    val bio: String?
    val fullName: String?
    val id: String?
}
