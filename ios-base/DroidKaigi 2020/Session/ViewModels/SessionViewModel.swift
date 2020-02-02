import ios_combined
import RealmSwift
import RxCocoa
import RxSwift

final class SessionViewModel {
    private let disposeBag = DisposeBag()

    // input
    private let viewDidAppearRelay: PublishRelay<Void>
    private let toggleEmbeddedViewRelay = PublishRelay<Void>()
    private let sessionsFetchFromApiRelay: BehaviorRelay<[Session]>
    private let sessionsFetchFromLocalRelay: BehaviorRelay<[AppBaseSession]>

    func toggleEmbeddedView() {
        toggleEmbeddedViewRelay.accept(())
    }

    func viewDidAppear() {
        viewDidAppearRelay.accept(())
    }

    // output
    let isFocusedOnEmbeddedView: Driver<Bool>
    let sessions: Observable<[AppBaseSession]>

    // dependencies
    private let bookingSessionProvider: BookingSessionProvider

    init() {
        viewDidAppearRelay = .init()
        sessionsFetchFromApiRelay = .init(value: [])
        sessionsFetchFromLocalRelay = .init(value: [])
        bookingSessionProvider = .init()
        let isFocusedOnEmbeddedViewRelay = BehaviorRelay<Bool>(value: true)
        isFocusedOnEmbeddedView = isFocusedOnEmbeddedViewRelay.asDriver()

        sessions = Observable.combineLatest(
            sessionsFetchFromApiRelay
                .asObservable()
                .map { sessions in
                    sessions.compactMap { (session) -> AppBaseSession? in
                        if let session = session as? SpeechSession {
                            return AppSpeechSession(session: session)
                        } else if let session = session as? ServiceSession {
                            return AppServiceSession(session: session)
                        }
                        return nil
                    }
                },
            sessionsFetchFromLocalRelay
                .asObservable()
        ).map { (arg: ([AppBaseSession], [AppBaseSession])) -> [AppBaseSession] in
            var (remote, local) = arg
            local = local
                .compactMap { $0 as? Object }
                .filter { !$0.isInvalidated }
                .compactMap { $0 as? AppBaseSession }
            remote = remote
                .compactMap { $0 as? Object }
                .filter { !$0.isInvalidated }
                .compactMap { $0 as? AppBaseSession }
            for (index, session) in remote.enumerated() {
                if let sameSession = local.lazy.first(where: { $0.id?.id == session.id?.id }) {
                    remote.remove(at: index)
                    remote.insert(sameSession, at: index)
                }
            }
            return remote
        }

        let dataProvider = SessionDataProvider()
        dataProvider
            .fetchSessions()
            .asObservable()
            .bind(to: sessionsFetchFromApiRelay)
            .disposed(by: disposeBag)

        toggleEmbeddedViewRelay.asObservable()
            .withLatestFrom(isFocusedOnEmbeddedViewRelay)
            .map { !$0 }
            .bind(to: isFocusedOnEmbeddedViewRelay)
            .disposed(by: disposeBag)

        viewDidAppearRelay.asObservable().subscribe(onNext: {
            let localSessions = self.bookingSessionProvider.fetchBookedSessions()
            self.sessionsFetchFromLocalRelay.accept(localSessions)
        }).disposed(by: disposeBag)
    }

    func bookSession(_ session: AppBaseSession) {
        bookingSessionProvider.bookSession(session: session).subscribe(onSuccess: {
            self.sessionsFetchFromLocalRelay.accept(self.bookingSessionProvider.fetchBookedSessions())
        }).disposed(by: disposeBag)
    }

    func resignBookingSession(_ session: AppBaseSession) {
        bookingSessionProvider.resignBookingSession(session).subscribe(onSuccess: {
            self.sessionsFetchFromLocalRelay.accept(self.bookingSessionProvider.fetchBookedSessions())
        }).disposed(by: disposeBag)
    }
}
