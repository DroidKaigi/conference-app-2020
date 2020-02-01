import ios_combined
import RealmSwift

protocol SessionUIModel {
    var dayNumber: Int32 { get }
    var desc: String { get }
    var endTime: Double { get }
    var hasIntendedAudience: Bool { get }
    var hasSpeaker: Bool { get }
    var isOnGoing: Bool { get }
    var isFinished: Bool { get }
    var startTimeText: String { get }
    var timeInMinutes: Int32 { get }

    var roomName: String { get }
    var pureId: String { get }
    var pureTitle: String { get }
    var pureTitleSummary: String { get }
    var isLocal: Bool { get }
}

extension SessionUIModel where Self: Session {
    var isLocal: Bool {
        false
    }

    var pureTitleSummary: String {
        timeSummary(lang: .ja, timezoneOffset: TimeZoneOffsetKt.defaultTimeZoneOffset())
    }
}

extension SessionUIModel where Self: LocalSession {
    var isLocal: Bool {
//        return !isInvalidated
        return true
    }
}

extension Session: SessionUIModel {
    var pureId: String {
        id.id
    }

    var pureTitle: String {
        if Locale.current == .init(identifier: "ja_JP") {
            return title.ja
        }
        return title.en
    }

    var roomName: String {
        if Locale.current == .init(identifier: "ja_JP") {
            return room.name.ja
        }
        return room.name.en
    }
}

final class LocalSession: Object, SessionUIModel {
    @objc dynamic var dayNumber: Int32 = 0
    @objc dynamic var desc: String = ""
    @objc dynamic var endTime: Double = 0
    @objc dynamic var hasIntendedAudience: Bool = false
    @objc dynamic var hasSpeaker: Bool = false
    @objc dynamic var isFavorited: Bool = false
    @objc dynamic var isOnGoing: Bool = false
    @objc dynamic var isFinished: Bool = false
    @objc dynamic var startTimeText: String = ""
    @objc dynamic var timeInMinutes: Int32 = 0

    @objc dynamic var roomName: String = ""
    @objc dynamic var pureId: String = ""
    @objc dynamic var pureTitle: String = ""
    @objc dynamic var pureTitleSummary: String = ""

    init(session: Session) {
        dayNumber = session.dayNumber
        desc = session.desc
        endTime = session.endTime
        hasIntendedAudience = session.hasIntendedAudience
        hasSpeaker = session.hasSpeaker
        isFavorited = session.isFavorited
        isOnGoing = session.isOnGoing
        isFinished = session.isFinished
        startTimeText = session.startTimeText
        timeInMinutes = session.timeInMinutes
        roomName = session.roomName
        pureTitle = session.pureTitle
        pureId = session.pureId
        pureTitleSummary = session.pureTitleSummary
    }

    required init() {
        super.init()
    }
}
