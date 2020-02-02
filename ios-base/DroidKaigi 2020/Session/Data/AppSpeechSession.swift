import ios_combined
import RealmSwift

final class AppSpeechSession: Object {
    @objc dynamic var id: AppSessionId = .init()
    @objc dynamic var dayNumber: Int = 0
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
    @objc dynamic var speakers: [Speaker] = []
    @objc dynamic var message: LocaledString?
    
    var lang: AppLang {
        .init(lang: rawLang)
    }
}
