import ios_combined
import RxCocoa
import RxSwift

final class SessionViewModel {
    private let disposeBag = DisposeBag()

    // input
    private let viewDidLoadRelay = PublishRelay<Void>()
    private let toggleEmbeddedViewRelay = PublishRelay<Void>()
    private let localSessionsRelay = BehaviorRelay<[LocalSession]>(value: [])

    func viewDidLoad() {
        viewDidLoadRelay.accept(())
    }

    func toggleEmbeddedView() {
        toggleEmbeddedViewRelay.accept(())
    }

    // output
    let isFocusedOnEmbeddedView: Driver<Bool>
    let sessions: Driver<[Session]>
    var localSessions: Driver<[LocalSession]> {
        localSessionsRelay.asDriver()
    }

    // dependencies
    private let bookingSessionProvider: BookingSessionProvider = .init()

    init() {
        let isFocusedOnEmbeddedViewRelay = BehaviorRelay<Bool>(value: true)
        let sessionsRelay = BehaviorRelay<[Session]>(value: [])

        isFocusedOnEmbeddedView = isFocusedOnEmbeddedViewRelay.asDriver()
        sessions = sessionsRelay.asDriver()

        let dataProvider = SessionDataProvider()

        viewDidLoadRelay.asObservable()
            .share()
            .flatMap { dataProvider.fetchSessions() }
            .bind(to: sessionsRelay)
            .disposed(by: disposeBag)

        viewDidLoadRelay.asObservable()
            .share()
            .flatMap { self.bookingSessionProvider.fetchBookedSessions() }
            .bind(to: localSessionsRelay)
            .disposed(by: disposeBag)

        toggleEmbeddedViewRelay.asObservable()
            .withLatestFrom(isFocusedOnEmbeddedViewRelay)
            .map { !$0 }
            .bind(to: isFocusedOnEmbeddedViewRelay)
            .disposed(by: disposeBag)
    }

    func bookSession(_ session: Session) -> Completable {
        return bookingSessionProvider.bookSession(session: session).observeOn(MainScheduler.instance)
    }

    func resignBookingSession(_ session: Session) -> Completable {
        let localSessions = localSessionsRelay.value
        if let session = localSessions.lazy.first(where: { $0.id == session.id.id }) {
            return bookingSessionProvider.resignBookingSession(session)
        }
        return .empty()
    }
}
