import ios_combined
import RealmSwift

final class LocalSession: Object {
    @objc dynamic var dayNumber: Int = 0
    @objc dynamic var desc: String = ""
    @objc dynamic var endTime: Double = 0
    @objc dynamic var hasIntendedAudience: Bool = false
    @objc dynamic var hasSpeaker: Bool = false
    @objc dynamic var id: String = ""
    @objc dynamic var isFavorited: Bool = false
    @objc dynamic var isOnGoing: Bool = false
    @objc dynamic var isFinished: Bool = false

    init(session: Session) {
        dayNumber = Int(session.dayNumber)
        desc = session.desc
        endTime = session.endTime
        hasIntendedAudience = session.hasIntendedAudience
        hasSpeaker = session.hasSpeaker
        id = session.id.id
        isFavorited = session.isFavorited
        isOnGoing = session.isOnGoing
        isFinished = session.isFinished
    }

    required init() {
        super.init()
    }
}
