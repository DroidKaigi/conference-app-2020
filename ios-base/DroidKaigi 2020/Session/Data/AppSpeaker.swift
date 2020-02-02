import ios_combined
import RealmSwift

final class AppSpeaker: Object {
    @objc dynamic var id: AppSpeakerId?
    @objc dynamic var name: String = ""
    @objc dynamic var tagLine: String?
    @objc dynamic var bio: String?
    @objc dynamic var imageUrl: String?

    init(speaker: Speaker) {
        id = .init(speakerId: speaker.id)
        name = speaker.name
        tagLine = speaker.tagLine
        bio = speaker.bio
        imageUrl = speaker.imageUrl
    }

    required init() {
        super.init()
    }
}
