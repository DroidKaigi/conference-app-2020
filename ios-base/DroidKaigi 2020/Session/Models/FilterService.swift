import ios_combined

protocol FilterServiceProtocol {
    func filterSessions(_ sessions: [Session], by sessionContents: FilterSessionContents) -> [Session]
}

final class FilterService: FilterServiceProtocol {
    func filterSessions(_ sessions: [Session], by sessionContents: FilterSessionContents) -> [Session] {
        return sessions.filter { session -> Bool in
            if sessionContents.isEmpty {
                return true
            }
            switch session {
            case is ServiceSession:
                return true
            case let speakerSession as SpeechSession:
                return sessionContents.rooms.map({ $0.id }).contains(speakerSession.room.id)
                    || sessionContents.langs.contains(speakerSession.lang)
                    || !sessionContents.levels.filter({ speakerSession.levels.contains($0) }).isEmpty
                    || sessionContents.categories.map({ $0.id }).contains(speakerSession.category.id)
                    || sessionContents.langSupports.contains(LangSupport.interpretation)
                        ? speakerSession.isInterpretationTarget : true
            default:
                return true
            }
        }
    }
}
