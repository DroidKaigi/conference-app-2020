package io.github.droidkaigi.confsched2020.data.db.entity

interface SpeakerEntity {
    var id: String
    var name: String
    var tagLine: String?
    var bio: String?
    var imageUrl: String?
}
