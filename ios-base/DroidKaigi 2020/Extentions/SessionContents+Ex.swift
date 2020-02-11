import ios_combined

extension SessionContents {
    static func empty() -> SessionContents {
        return .init(sessions: [], speakers: [], rooms: [], langs: [], langSupports: [], category: [], levels: [])
    }
}

