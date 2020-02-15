import ioscombined

extension SearchResult {
    static func empty() -> SearchResult {
        return .init(sessions: [], speakers: [], query: "")
    }
}
