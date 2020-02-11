import ioscombined
import RxCocoa
import RxSwift

protocol SearchViewModelType {
    // Input
    func search(query: String)
    func clear()
    func bookSession(_ session: Session)
    func resignBookingSession(_ session: Session)

    // Output
    var searchResult: Driver<SearchResult> { get }
}

final class SearchViewModel: SearchViewModelType {
    // Input
    private let searchRelay = BehaviorRelay<String>(value: "")

    // Output
    private let searchResultRelay = BehaviorRelay<SearchResult>(value: .empty())
    private let sessionContentsRelay = BehaviorRelay<SessionContents>(value: .empty())

    // dependencies
    private let bookingSessionProvider = BookingSessionProvider()

    private let disposeBag = DisposeBag()

    var searchResult: Driver<SearchResult>

    init() {
        searchResult = searchResultRelay.asDriver()

        let dataProvider = SessionDataProvider()
        dataProvider.fetchSessionContents()
            .subscribe(onSuccess: { [weak self] sessionContents in
                self?.sessionContentsRelay.accept(sessionContents)
                self?.searchRelay.accept("")
            }, onError: nil)
            .disposed(by: disposeBag)

        let searched = searchRelay.asObservable()
            .withLatestFrom(sessionContentsRelay) { ($0, $1) }
            .map { args -> SearchResult in
                let (query, sessionContents) = args
                return sessionContents.search(query: query)
            }
            .share()
        let changeBookingSessions = bookingSessionProvider.fetchBookedSessions()
            .withLatestFrom(searchRelay)
            .withLatestFrom(sessionContentsRelay) { ($0, $1) }
            .map { args -> SearchResult in
                let (query, sessionContents) = args
                return sessionContents.search(query: query)
            }
            .share()

        Observable.merge(searched, changeBookingSessions)
            .withLatestFrom(bookingSessionProvider.fetchBookedSessions()) { ($0, $1) }
            .map { (args) -> SearchResult in
                let (searchResult, bookingSessions) = args
                let sessions = searchResult.sessions.map { session -> Session in
                    if let bookingSession = bookingSessions.first(where: { bookingSession -> Bool in
                        bookingSession.id == session.id
                    }) {
                        return bookingSession
                    }
                    return session
                }
                return SearchResult(sessions: sessions, speakers: searchResult.speakers, query: "")
            }
            .bind(to: searchResultRelay)
            .disposed(by: disposeBag)
    }

    func search(query: String) {
        searchRelay.accept(query)
    }

    func clear() {
        searchRelay.accept("")
    }

    func bookSession(_ session: Session) {
        bookingSessionProvider.bookSession(session)
    }

    func resignBookingSession(_ session: Session) {
        bookingSessionProvider.resignBookingSession(session.id.id)
    }
}
