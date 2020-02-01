import ios_combined
import RxCocoa
import RxSwift

final class SessionViewModel {
    private let disposeBag = DisposeBag()

    // input
    private let viewDidLoadRelay = PublishRelay<Void>()
    private let toggleEmbeddedViewRelay = PublishRelay<Void>()

    func viewDidLoad() {
        viewDidLoadRelay.accept(())
    }

    func toggleEmbeddedView() {
        toggleEmbeddedViewRelay.accept(())
    }

    // output
    let isFocusedOnEmbeddedView: Driver<Bool>
    let sessions: Driver<[SessionUIModel]>

    // dependencies
    private let bookingSessionProvider: BookingSessionProvider = .init()

    init() {
        let isFocusedOnEmbeddedViewRelay = BehaviorRelay<Bool>(value: true)
        let sessionsRelay = BehaviorRelay<[SessionUIModel]>(value: [])
        sessions = sessionsRelay.asDriver()
        isFocusedOnEmbeddedView = isFocusedOnEmbeddedViewRelay.asDriver()

        let dataProvider = SessionDataProvider()

        let remoteSessions = viewDidLoadRelay.asObservable()
            .share()
            .flatMap {
                dataProvider.fetchSessions()
                    .asObservable()
                    .map { $0.compactMap { $0 as SessionUIModel } }
            }

        let localSessions = bookingSessionProvider
            .fetchBookedSessions().asObservable()
            .map { $0.compactMap { $0 as SessionUIModel } }

        let combinedSession = Observable.combineLatest(remoteSessions, localSessions).map { (arg: ([SessionUIModel], [SessionUIModel])) -> [SessionUIModel] in
            var (remote, local) = arg
            if let bookmarkedSessionIndex = remote.firstIndex(where: { local.map { $0.pureId }.contains($0.pureId) }) {
                remote.remove(at: bookmarkedSessionIndex)
            }
            return remote + local
        }

        viewDidLoadRelay.asObservable()
            .withLatestFrom(combinedSession)
            .bind(to: sessionsRelay)
            .disposed(by: disposeBag)

        toggleEmbeddedViewRelay.asObservable()
            .withLatestFrom(isFocusedOnEmbeddedViewRelay)
            .map { !$0 }
            .bind(to: isFocusedOnEmbeddedViewRelay)
            .disposed(by: disposeBag)
    }

    func bookSession(_ session: SessionUIModel) -> Completable {
        if var session = session as? Session {
            return bookingSessionProvider.bookSession(session: session).observeOn(MainScheduler.instance)
        }
        return .empty()
    }

    func resignBookingSession(_ session: SessionUIModel) -> Completable {
        if var session = session as? LocalSession {
            return bookingSessionProvider.resignBookingSession(session)
        }
        return .empty()
    }
}
