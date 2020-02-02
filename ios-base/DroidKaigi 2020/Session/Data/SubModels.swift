import ios_combined
import RealmSwift

// MARK: - ID
final class AppSessionId: Object {
    @objc dynamic var id: String = ""
}

final class AppSpeakerId: Object {
    @objc dynamic var id: String = ""
}

// MARK: - LocaledString
final class AppLocaledString: Object {
    @objc dynamic var ja: String = ""
    @objc dynamic var en: String = ""
}

// MARK: - Room
final class AppRoom: Object {
    @objc dynamic var id: Int = 0
    @objc dynamic var name: AppLocaledString = .init()
    @objc dynamic var sort: Int = 0
}


// MARK: - Category
final class AppCategory: Object {
    @objc dynamic var id: Int = 0
    @objc dynamic var name: AppLocaledString = .init()
}


// MARK: - AppLang
enum AppLang: String {
    case ja = "JA"
    case en = "EN"
    
    init(lang: Lang) {
        if lang == Lang.ja {
            self = .ja
        } else {
            self = .en
        }
    }
    
    init(lang: String) {
        if lang == "ja" {
            self = .ja
        } else {
            self = .en
        }
    }
}
