import Foundation
import ios_combined

extension Session {
    var timeRoomText: String {
        "\(timeInMinutes)min / \(room.name.ja)"
    }
}
