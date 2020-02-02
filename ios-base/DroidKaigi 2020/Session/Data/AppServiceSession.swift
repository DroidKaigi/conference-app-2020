import ios_combined
import RealmSwift
//
///// same as Session for Kotlin-Native
//protocol ApplicationSession {
//    var id: String { get }
//    var title: String { get }
//    var enTitle: String { get }
//    var desc: String { get }
//    var stime: Int { get }
//    var etime: Int { get }
//    var language: String { get }
//    var category: ApplicationCategoryEntity? { get }
//    var intendedAudience: String? { get }
//    var videoUrl: String? { get }
//    var slideUrl: String? { get }
//    var isInterpretationTarget: Bool { get }
//    var room: ApplicationRoomEntity? { get }
//    var message: ApplicationMessageEntity? { get }
//    var isServiceSession: Bool { get }
//    var sessionType: String? { get }
//}

//protocol ApplicationMessageEntity {
//    var ja: String { get }
//    var en: String { get }
//}
//
//protocol ApplicationRoomEntity {
//    var id: Int { get }
//    var name: String { get }
//    var enName: String { get }
//    var sort: Int { get }
//}
//
//protocol ApplicationCategoryEntity {
//    var id: Int { get }
//    var name: String { get }
//    var enName: String { get }
//}


//
///// same as SpeakerSession for Kotlin-Native
//final class ApplicationSpeakerSession: Object {
//
//    init(session: ) {
//
//    }
//
//    required init() {
//        super.init()
//    }
//
//}

/// same as ServiceSession for Kotlin-Native
final class AppServiceSession: Object {
    @objc dynamic var dayNumber: Int32 = 0
    @objc dynamic var desc: String = ""
    @objc dynamic var startTime: Double = 0
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

    init(session: ServiceSession) {
        dayNumber = session.dayNumber
        desc = session.desc
        startTime = session.startTime
        endTime = session.endTime
        hasIntendedAudience = session.hasIntendedAudience
        hasSpeaker = session.hasSpeaker
        isFavorited = session.isFavorited
        isOnGoing = session.isOnGoing
        isFinished = session.isFinished
        startTimeText = session.startTimeText
        timeInMinutes = session.timeInMinutes
        pureId = session.id.id
        // TODO: Select Localized Lang if desired.
        roomName = session.room.name.ja
        pureTitle = session.title.ja
        pureTitleSummary = session.summary(lang: .ja, timezoneOffset: TimeZoneOffsetKt.defaultTimeZoneOffset())
    }

    required init() {
        super.init()
    }
}
