import ioscombined

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
                let roomsOk = sessionContents.rooms.isEmpty
                    || sessionContents.rooms.map { $0.id }.contains(speakerSession.room.id)
                let langsOk = sessionContents.langs.isEmpty
                    || sessionContents.langs.contains(speakerSession.lang)
                let levelOk = sessionContents.levels.isEmpty
                    || !sessionContents.levels.filter { speakerSession.levels.contains($0) }.isEmpty
                let categoryOk = sessionContents.categories.isEmpty
                    || sessionContents.categories.map { $0.id }.contains(speakerSession.category.id)
                let langSupportOk = sessionContents.langSupports.isEmpty
                    || (sessionContents.langSupports.contains(LangSupport.interpretation)
                        ? speakerSession.isInterpretationTarget : true)
                return roomsOk
                    && langsOk
                    && levelOk
                    && categoryOk
                    && langSupportOk
            default:
                return true
            }
        }
    }
}
