import ios_combined
import RealmSwift

// MARK: - initialized with ServiceSession of api-model.

final class AppServiceSession: Object {
    @objc dynamic var id: AppSessionId?
    @objc dynamic var dayNumber: Int32 = 0
    @objc dynamic var desc: String = ""
    @objc dynamic var startTime: TimeInterval = 0
    @objc dynamic var endTime: TimeInterval = 0
    @objc dynamic var title: AppLocaledString?
    @objc dynamic var room: AppRoom?
    @objc dynamic var isFavorited: Bool = false

    init(session: ServiceSession) {
        id = .init(sessionId: session.id)
        dayNumber = session.dayNumber
        desc = session.desc
        startTime = session.startTime
        endTime = session.endTime
        title = .init(localedString: session.title)
        room = .init(room: session.room)
        isFavorited = session.isFavorited
    }

    required init() {
        super.init()
    }
}
