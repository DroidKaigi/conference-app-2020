import ios_combined
import RxCocoa
import RxSwift

final class SessionViewModel {
    private let disposeBag = DisposeBag()

    // input
    private let toggleEmbeddedViewRelay = PublishRelay<Void>()
    private let remoteSessionsRelay: BehaviorRelay<[Session]> = .init(value: [])
    private let localSessionsRelay: BehaviorRelay<[LocalSession]> = .init(value: [])
    private let updateLocalSessionsRelay: PublishRelay<Void> = .init()

    func toggleEmbeddedView() {
        toggleEmbeddedViewRelay.accept(())
    }

    // output
    let isFocusedOnEmbeddedView: Driver<Bool>
    lazy var sessions: Driver<[SessionUIModel]> = {
        Observable.combineLatest(remoteSessionsRelay.asObservable(), localSessionsRelay.asObservable()).map { remoteSessions, localSessions -> [SessionUIModel] in
            var totalSessions: [SessionUIModel] = remoteSessions

            for localSession in localSessions {
                if let index = remoteSessions.lazy.firstIndex(where: { $0.pureId == localSession.pureId }) {
                    totalSessions.remove(at: index)
                    totalSessions.insert(localSession, at: index)
                }
            }
            return totalSessions
        }.asDriver(onErrorJustReturn: [])
    }()

    // dependencies
    private let bookingSessionProvider: BookingSessionProvider = .init()

    init() {
        let isFocusedOnEmbeddedViewRelay = BehaviorRelay<Bool>(value: true)
        isFocusedOnEmbeddedView = isFocusedOnEmbeddedViewRelay.asDriver()

        let dataProvider = SessionDataProvider()

        dataProvider
            .fetchSessions()
            .asObservable()
            .bind(to: remoteSessionsRelay)
            .disposed(by: disposeBag)

        bookingSessionProvider
            .fetchBookedSessions()
            .bind(to: localSessionsRelay)
            .disposed(by: disposeBag)

        toggleEmbeddedViewRelay.asObservable()
            .withLatestFrom(isFocusedOnEmbeddedViewRelay)
            .map { !$0 }
            .bind(to: isFocusedOnEmbeddedViewRelay)
            .disposed(by: disposeBag)
    }

    func bookSession(_ session: SessionUIModel) -> Observable<Void> {
        if let session = session as? Session {
            return bookingSessionProvider.bookSession(session: session).asObservable()
        }
        return .empty()
    }

    func resignBookingSession(_ session: SessionUIModel) -> Observable<Void> {
        if let session = session as? LocalSession {
            return bookingSessionProvider.resignBookingSession(session).asObservable()
        }
        return .empty()
    }
}
