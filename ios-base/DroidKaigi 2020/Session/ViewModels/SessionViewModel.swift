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
    private let sessionsFetchFromLocalRelay: BehaviorRelay<[Session]>

    func toggleEmbeddedView() {
        toggleEmbeddedViewRelay.accept(())
    }

    func viewDidAppear() {
        viewDidAppearRelay.accept(())
    }

    // output
    let isFocusedOnEmbeddedView: Driver<Bool>
    let sessions: Driver<[Session]>

    // dependencies
    private let bookingSessionProvider: BookingSessionProvider

    init() {
        viewDidAppearRelay = .init()
        sessionsFetchFromApiRelay = .init(value: [])
        sessionsFetchFromLocalRelay = .init(value: [])
        bookingSessionProvider = .init()
        let isFocusedOnEmbeddedViewRelay = BehaviorRelay<Bool>(value: true)
        isFocusedOnEmbeddedView = isFocusedOnEmbeddedViewRelay.asDriver()

        sessions = Driver.combineLatest(
            sessionsFetchFromApiRelay.asDriver(onErrorJustReturn: []),
            sessionsFetchFromLocalRelay.asDriver()
        ) { remote, local in
            let filteredSameSession = remote.filter { (session: Session) in
                !local.contains(where: { $0.isFavorited && session.id.id == $0.id.id })
            }
            return (filteredSameSession + local).sorted { $0.startTime < $1.startTime }
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

        sessionsFetchFromApiRelay
            .asObservable()
            .filter { !$0.isEmpty }
            .take(1)
            .subscribe(onNext: { [weak self] sessions in
                guard let self = self, let firstSession = sessions.first else {
                    return
                }
                self.bookingSessionProvider
                    .fetchBookedSessions(firstSession: firstSession)
                    .bind(to: self.sessionsFetchFromLocalRelay)
                    .disposed(by: self.disposeBag)
            }).disposed(by: disposeBag)
    }

    func bookSession(_ session: Session) {
        bookingSessionProvider.bookSession(session: session)
    }

    func resignBookingSession(_ session: Session) {
        bookingSessionProvider.resignBookingSession(session.id.id)
    }
}
