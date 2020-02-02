import ios_combined
import RealmSwift

// MARK: - initialized with SpeechSession of api-model.

final class AppSpeechSession: Object {
    @objc dynamic var id: AppSessionId = .init()
    @objc dynamic var dayNumber: Int32 = 0
    @objc dynamic var startTime: TimeInterval = 0
    @objc dynamic var endTime: TimeInterval = 0
    @objc dynamic var title: AppLocaledString = .init()
    @objc dynamic var desc: String = ""
    @objc dynamic var room: AppRoom = .init()
    @objc dynamic var rawLang: String = ""
    @objc dynamic var category: AppCategory = .init()
    @objc dynamic var intendedAudience: String?
    @objc dynamic var videoUrl: String?
    @objc dynamic var slideUrl: String?
    @objc dynamic var isInterpretationTarget: Bool = false
    @objc dynamic var isFavorited: Bool = false
    @objc dynamic var speakers: [AppSpeaker] = []
    @objc dynamic var message: LocaledString?

    var lang: Lang {
        get {
            if rawLang == "ja" {
                return .ja
            } else {
                return .en
            }
        }
        set {
            rawLang = newValue.text.getByLang(lang: newValue)
        }
    }

    init(session: SpeechSession) {
        id = .init(sessionId: session.id)
        dayNumber = session.dayNumber
        startTime = session.startTime
        endTime = session.endTime
        title = .init(localedString: session.title)
        desc = session.desc
        room = .init(room: session.room)
        category = .init(category: session.category)
        intendedAudience = session.intendedAudience
        videoUrl = session.videoUrl
        slideUrl = session.slideUrl
        isInterpretationTarget = session.isInterpretationTarget
        isFavorited = session.isFavorited
        speakers = session.speakers.map { AppSpeaker(speaker: $0) }
        message = session.message
        super.init()
        lang = session.lang
    }

    required init() {
        super.init()
    }
}
