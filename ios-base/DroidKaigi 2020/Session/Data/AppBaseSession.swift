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
    var isFavorited: Bool { get set }
    var startDayText: String { get }
    var startTimeText: String { get }
    var isFinished: Bool { get }
    var isOnGoing: Bool { get }
    var timeInMinutes: Int32 { get }
    var hasIntendedAudience: Bool { get }
    var hasSpeaker: Bool { get }
    var shouldCountForFilter: Bool { get }
    var timeRoomText: String { get }

    func shortSummary(lang: Lang) -> String
}

extension AppServiceSession: AppBaseSession {
    var isFinished: Bool {
        Date().timeIntervalSince1970 > endTime
    }

    var isOnGoing: Bool {
        (startTime ... endTime).contains(Date().timeIntervalSince1970)
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

    func shortSummary(lang: Lang) -> String {
        "\(timeInMinutes)min / \(room?.name?.getByLang(lang) ?? "")"
    }
}

extension AppSpeechSession: AppBaseSession {
    var isFinished: Bool {
        Date().timeIntervalSince1970 > endTime
    }

    var isOnGoing: Bool {
        (startTime ... endTime).contains(Date().timeIntervalSince1970)
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

    func shortSummary(lang: Lang) -> String {
        "\(timeInMinutes)min / \(room?.name?.getByLang(lang) ?? "")"
    }
}
