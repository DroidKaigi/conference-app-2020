import ios_combined

extension SessionContents {
    static func empty() -> SessionContents {
        return SessionContents(sessions: [], speakers: [], rooms: [], langs: [], langSupports: [], category: [], levels: [])
    }
}
