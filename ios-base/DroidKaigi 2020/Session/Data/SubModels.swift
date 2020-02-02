import ios_combined
import RealmSwift

// MARK: - ID

final class AppSessionId: Object {
    @objc dynamic var id: String = ""

    init(sessionId: SessionId) {
        id = sessionId.id
    }

    required init() {
        super.init()
    }
}

final class AppSpeakerId: Object {
    @objc dynamic var id: String = ""

    init(speakerId: SpeakerId) {
        id = speakerId.id
    }

    required init() {
        super.init()
    }
}

// MARK: - LocaledString

final class AppLocaledString: Object {
    @objc dynamic var ja: String = ""
    @objc dynamic var en: String = ""

    init(localedString: LocaledString) {
        ja = localedString.ja
        en = localedString.en
    }

    required init() {
        super.init()
    }

    func getByLang(_ lang: Lang) -> String {
        if lang == .ja {
            return ja
        }
        return en
    }
}

// MARK: - Room

final class AppRoom: Object {
    @objc dynamic var id: Int32 = 0
    @objc dynamic var name: AppLocaledString = .init()
    @objc dynamic var sort: Int32 = 0

    init(room: Room) {
        id = room.id
        name = AppLocaledString(localedString: room.name)
        sort = room.sort
    }

    required init() {
        super.init()
    }
}

// MARK: - Category

final class AppCategory: Object {
    @objc dynamic var id: Int32 = 0
    @objc dynamic var name: AppLocaledString = .init()

    init(category: ios_combined.Category) {
        id = category.id
        name = .init(localedString: category.name)
    }

    required init() {
        super.init()
    }
}
