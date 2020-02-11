import ioscombined

extension LocaledString {
    var currentLangString: String {
        guard let currentLang = Locale.preferredLanguages.first else {
            return en
        }
        switch currentLang.prefix(2) {
        case "ja":
            return ja
        case "en":
            return en
        default:
            return en
        }
    }
}
