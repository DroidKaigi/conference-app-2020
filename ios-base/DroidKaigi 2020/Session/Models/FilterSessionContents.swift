import ios_combined

struct FilterSessionContents {
    var rooms: [Room]
    var langs: [Lang]
    var levels: [Level]
    var categories: [ios_combined.Category]
    var langSupports: [LangSupport]

    init(rooms: [Room], langs: [Lang], levels: [Level], categories: [ios_combined.Category], langSupports: [LangSupport]) {
        self.rooms = rooms
        self.langs = langs
        self.levels = levels
        self.categories = categories
        self.langSupports = langSupports
    }

    init(sessionContents: SessionContents) {
        self.rooms = sessionContents.rooms
        self.langs = sessionContents.langs
        self.levels = sessionContents.levels
        self.categories = sessionContents.category
        self.langSupports = sessionContents.langSupports
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
