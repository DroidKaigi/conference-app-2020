import Foundation
import ioscombined

extension Session {
    var timeRoomText: String {
        "\(timeInMinutes)min / \(room.name.currentLangString)"
    }

    /// e.g. 2/20
    var startMonthAndDayText: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "M/dd"
        let startDate = Date(timeIntervalSince1970: startTime / 1000)
        return formatter.string(from: startDate)
    }

    var currentLangShortSummary: String {
        guard let currentLang = Locale.preferredLanguages.first else {
            return shortSummary(lang: .en)
        }
        switch currentLang.prefix(2) {
        case "ja":
            return shortSummary(lang: .ja)
        case "en":
            return shortSummary(lang: .en)
        default:
            return shortSummary(lang: .en)
        }
    }
}
