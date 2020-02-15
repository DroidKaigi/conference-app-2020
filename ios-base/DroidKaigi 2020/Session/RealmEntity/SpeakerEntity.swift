import ioscombined
import RealmSwift

class SpeakerEntity: Object {
    @objc dynamic var id: String = ""
    @objc dynamic var name: String = ""
    @objc dynamic var tagLine: String?
    @objc dynamic var bio: String?
    @objc dynamic var imageUrl: String?

    init(speaker: Speaker) {
        id = speaker.id.id
        name = speaker.name
        tagLine = speaker.tagLine
        bio = speaker.bio
        imageUrl = speaker.imageUrl
    }

    required init() {
        super.init()
    }
}
