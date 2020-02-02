import ios_combined

/*
 Because of using RealmSwift, [id, room, title] which are not primitive type have to be optional.
 */
protocol AppBaseSession {
    var id: AppSessionId? { get }
    var title: AppLocaledString? { get }
    var desc: String { get }
    var dayNumber: Int32 { get }
    var startTime: TimeInterval { get }
    var endTime: TimeInterval { get }
    var room: AppRoom? { get }
    var isFavorited: Bool { get }
    var startDayText: String { get }
    var startTimeText: String { get }
    var isFinished: Bool { get }
    var isOnGoing: Bool { get }
    var timeInMinutes: Int { get }
    var hasIntendedAudience: Bool { get }
    var hasSpeaker: Bool { get }
    var shouldCountForFilter: Bool { get }
    var timeRoomText: String { get }

    func shortSummary(lang: Lang) -> String
}

extension AppServiceSession: AppBaseSession {
    var startDayText: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy.M.d"
        return formatter.string(from: Date(timeIntervalSince1970: startTime))
    }

    var startTimeText: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: Date(timeIntervalSince1970: startTime))
    }

    var isFinished: Bool {
        Date().timeIntervalSince1970 > endTime
    }

    var isOnGoing: Bool {
        (startTime ... endTime).contains(Date().timeIntervalSince1970)
    }

    var timeInMinutes: Int {
        Int(endTime.distance(to: startTime) / 60)
    }

    var hasIntendedAudience: Bool {
        true
    }

    var hasSpeaker: Bool {
        true
    }

    var shouldCountForFilter: Bool {
        true
    }

    var timeRoomText: String {
        "\(timeInMinutes)min / \(room?.name?.ja ?? "")"
    }

    func shortSummary(lang: Lang) -> String {
        "\(timeInMinutes)min / \(room?.name?.getByLang(lang) ?? "")"
    }
}

extension AppSpeechSession: AppBaseSession {
    var startDayText: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy.M.d"
        return formatter.string(from: Date(timeIntervalSince1970: startTime))
    }

    var startTimeText: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: Date(timeIntervalSince1970: startTime))
    }

    var isFinished: Bool {
        Date().timeIntervalSince1970 > endTime
    }

    var isOnGoing: Bool {
        (startTime ... endTime).contains(Date().timeIntervalSince1970)
    }

    var timeInMinutes: Int {
        Int(endTime.distance(to: startTime) / 60)
    }

    var hasIntendedAudience: Bool {
        false
    }

    var hasSpeaker: Bool {
        false
    }

    var shouldCountForFilter: Bool {
        false
    }

    var timeRoomText: String {
        "\(timeInMinutes)min / \(room?.name?.ja ?? "")"
    }

    func shortSummary(lang: Lang) -> String {
        "\(timeInMinutes)min / \(room?.name?.getByLang(lang) ?? "")"
    }
}
