import ioscombined

struct FilterSessionContents {
    var rooms: [Room]
    var langs: [Lang]
    var levels: [Level]
    var categories: [ioscombined.Category]
    var langSupports: [LangSupport]

    init(rooms: [Room], langs: [Lang], levels: [Level], categories: [ioscombined.Category], langSupports: [LangSupport]) {
        self.rooms = rooms
        self.langs = langs
        self.levels = levels
        self.categories = categories
        self.langSupports = langSupports
    }

    init(sessionContents: SessionContents) {
        rooms = sessionContents.rooms
        langs = sessionContents.langs
        levels = sessionContents.levels
        categories = sessionContents.category
        langSupports = sessionContents.langSupports
    }

    static func empty() -> FilterSessionContents {
        return self.init(rooms: [], langs: [], levels: [], categories: [], langSupports: [])
    }

    var isEmpty: Bool {
        return rooms.isEmpty
            && langs.isEmpty
            && levels.isEmpty
            && categories.isEmpty
            && langSupports.isEmpty
    }
}
