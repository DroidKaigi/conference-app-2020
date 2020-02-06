import Foundation
import ios_combined

extension Session {
    var timeRoomText: String {
        "\(timeInMinutes)min / \(room.name.ja)"
    }

    /// e.g. 2/20
    var startMonthAndDayText: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "M/dd"
        let startDate = Date(timeIntervalSince1970: startTime / 1000)
        return formatter.string(from: startDate)
    }
}
