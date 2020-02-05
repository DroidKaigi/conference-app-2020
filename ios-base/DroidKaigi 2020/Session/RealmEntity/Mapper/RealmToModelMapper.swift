import ios_combined

final class RealmToModelMapper {
    static func toModel(sessionEntity session: SessionEntity, firstSessionSTime firstSTime: TimeInterval) -> Session? {
        let sessionTypeIds: [(String, SessionType)] = [
            ("normal", SessionType.normal),
            ("welcome_talk", SessionType.welcomeTalk),
            ("reserved", SessionType.reserved),
            ("codelabs", SessionType.codelabs),
            ("fireside_chat", SessionType.firesideChat),
            ("lunch", SessionType.lunch),
            ("break", SessionType.breaktime),
            ("after_party", SessionType.afterParty),
            ("unknown", SessionType.unknown),
        ]

        let calendar = Calendar(identifier: .gregorian)

        // FIXME: I want to use same logic as Android.
        let startDay = calendar.dateComponents([.year, .month, .day], from: Date(timeIntervalSince1970: TimeInterval(session.stime) / 1000)).day ?? 0
        let firstDay = calendar.dateComponents([.year, .month, .day], from: Date(timeIntervalSince1970: firstSTime / 1000)).day ?? 0
        let dayNumber = startDay - firstDay + 1

        guard let room = session.room else {
            return nil
        }
        let sessionTypeId = session.sessionType
        let sessionType = sessionTypeIds.first(where: { $0.0 == sessionTypeId })?.1
        let category = session.category

        if session.isServiceSession {
            return ServiceSession(
                id: SessionId(id: session.id),
                dayNumber: Int32(dayNumber),
                startTime: TimeInterval(session.stime),
                endTime: TimeInterval(session.etime),
                title: LocaledString(ja: session.title, en: session.enTitle),
                desc: session.desc,
                room: Room(
                    id: Int32(room.id),
                    name: LocaledString(ja: room.name, en: room.enName),
                    sort: Int32(room.sort)
                ),
                levels: [],
                sessionType: sessionType ?? SessionType.unknown,
                isFavorited: session.isFavorited
            )
        } else {
            return SpeechSession(
                id: SessionId(id: session.id),
                dayNumber: Int32(dayNumber),
                startTime: TimeInterval(session.stime),
                endTime: TimeInterval(session.etime),
                title: LocaledString(ja: session.title, en: session.enTitle),
                desc: session.desc,
                room: Room(
                    id: Int32(room.id),
                    name: LocaledString(ja: room.name, en: room.enName),
                    sort: Int32(room.sort)
                ),
                levels: [],
                lang: session.language == "ja" ? .ja : .en,
                category: Category(
                    id: Int32(category?.id ?? 0),
                    name: LocaledString(
                        ja: category?.name ?? "",
                        en: category?.enName ?? ""
                    )
                ),
                intendedAudience: session.intendedAudience,
                videoUrl: session.videoUrl,
                slideUrl: session.slideUrl,
                isInterpretationTarget: session.isInterpretationTarget,
                isFavorited: session.isFavorited,
                speakers: session.speakers.map { toModel(speakerEntity: $0) },
                message: nil
            )
        }
    }

    private static func toModel(speakerEntity speaker: SpeakerEntity) -> Speaker {
        Speaker(
            id: SpeakerId(id: speaker.id),
            name: speaker.name,
            tagLine: speaker.tagLine ?? "",
            bio: speaker.bio ?? "",
            imageUrl: speaker.imageUrl
        )
    }
}
