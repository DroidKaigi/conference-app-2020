import ios_combined
import RealmSwift

final class SessionEntity: Object {
    @objc dynamic var id: String = ""
    @objc dynamic var title: String = ""
    @objc dynamic var enTitle: String = ""
    @objc dynamic var desc: String = ""
    @objc dynamic var stime: Int = 0
    @objc dynamic var etime: Int = 0
    @objc dynamic var language: String = ""
    @objc dynamic var category: CategoryEntity?
    @objc dynamic var intendedAudience: String?
    @objc dynamic var videoUrl: String?
    @objc dynamic var slideUrl: String?
    @objc dynamic var isInterpretationTarget: Bool = false
    @objc dynamic var room: RoomEntity?
    @objc dynamic var isServiceSession: Bool = false
    @objc dynamic var sessionType: String?

    @objc dynamic var isFavorited: Bool = false

    let speakers: List<SpeakerEntity>

    init(session: Session) {
        let sessionTypeIds: [(String, SessionType)] = [
            ("normal", SessionType.normal),
            ("welcome_talk", SessionType.welcomeTalk),
            ("reserved", SessionType.reserved),
            ("codelabs", SessionType.codelabs),
            ("fireside_chat", SessionType.firesideChat),
            ("lunch", SessionType.lunch),
            ("break", SessionType.breaktime),
            ("after_party", SessionType.afterParty),
            ("unknown", SessionType.unknown),
        ]

        id = session.id.id
        title = session.title.ja
        enTitle = session.title.en
        desc = session.desc
        stime = Int(session.startTime)
        etime = Int(session.endTime)
        room = RoomEntity(room: session.room)
        isFavorited = session.isFavorited

        if let speech = session as? SpeechSession {
            // FIXME: I do not know what is lang.
            language = speech.lang == .ja ? "Japanese" : "english"
            category = CategoryEntity(category: speech.category)
            intendedAudience = speech.intendedAudience
            videoUrl = speech.videoUrl
            slideUrl = speech.slideUrl
            isInterpretationTarget = speech.isInterpretationTarget
            isServiceSession = false
            let speakers: List<SpeakerEntity> = List()
            speakers.append(objectsIn: speech.speakers.map { SpeakerEntity(speaker: $0) })
            self.speakers = speakers
        } else if let service = session as? ServiceSession {
            sessionType = sessionTypeIds.first(where: { $0.1 == service.sessionType })?.0
            speakers = .init()
        } else {
            fatalError()
        }
    }

    required init() {
        speakers = .init()
        super.init()
    }

    override class func primaryKey() -> String? {
        "id"
    }
}
