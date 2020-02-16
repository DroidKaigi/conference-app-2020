import ioscombined
import RealmSwift
import RxCocoa
import RxSwift

final class SessionViewModel {
    private let disposeBag = DisposeBag()

    // input
    private let sessionsFetchFromApiRelay: PublishRelay<[Session]>
    private let sessionsFetchFromLocalRelay: BehaviorRelay<[Session]>
    private let selectedFilterSessionContentsRelay: BehaviorRelay<FilterSessionContents>

    // output
    private let sessionsRelay: BehaviorRelay<[Session]>
    private let filteredSessionsRelay: BehaviorRelay<[Session]>
    let sessions: Driver<[Session]>

    // dependencies
    private let filterService: FilterServiceProtocol
    private let bookingSessionProvider: BookingSessionProvider

    init() {
        sessionsFetchFromApiRelay = .init()
        sessionsFetchFromLocalRelay = .init(value: [])
        selectedFilterSessionContentsRelay = .init(value: .empty())
        sessionsRelay = .init(value: [])
        filteredSessionsRelay = .init(value: [])
        filterService = FilterService()
        bookingSessionProvider = .init()

        sessions = filteredSessionsRelay.asDriver()

        Driver.combineLatest(
            sessionsFetchFromApiRelay.asDriver(onErrorJustReturn: []),
            sessionsFetchFromLocalRelay.asDriver()
        ) { remote, local in
            let filteredSameSession = remote.filter { (session: Session) in
                !local.contains(where: { (localSession: Session) in session.id.id == localSession.id.id })
            }
            return (filteredSameSession + local).sorted { (pre: Session, next: Session) in
                return pre.startTime == next.startTime ? pre.room.name.currentLangString <= next.room.name.currentLangString : pre.startTime < next.startTime
            }
        }
        .drive(sessionsRelay)
        .disposed(by: disposeBag)

        let dataProvider = SessionDataProvider()
        dataProvider
            .fetchSessions()
            .filter { !$0.isEmpty }
            .asObservable()
            .bind(to: sessionsFetchFromApiRelay)
            .disposed(by: disposeBag)

        sessionsFetchFromApiRelay
            .asObservable()
            .flatMap { [unowned self] (sessions: [Session]) -> Observable<[Session]> in
                if sessions.isEmpty {
                    return self.bookingSessionProvider.fetchBookedSessions()
                }
                return .just(sessions)
            }
            .filter { !$0.isEmpty }
            .compactMap { $0.first?.startTime }
            .flatMap(bookingSessionProvider.fetchBookedSessions)
            .bind(to: sessionsFetchFromLocalRelay)
            .disposed(by: disposeBag)

        Observable.combineLatest(selectedFilterSessionContentsRelay, sessionsRelay)
            .map { [weak self] args -> [Session] in
                guard let self = self else { return [] }
                let (sessionContents, sessions) = args
                return self.filterService.filterSessions(sessions, by: sessionContents)
            }
            .bind(to: filteredSessionsRelay)
            .disposed(by: disposeBag)
    }

    func selectedFilterSessionContents(_ sessionContents: FilterSessionContents) {
        selectedFilterSessionContentsRelay.accept(sessionContents)
    }

    func bookSession(_ session: Session) {
        bookingSessionProvider.bookSession(session)
    }

    func resignBookingSession(_ session: Session) {
        bookingSessionProvider.resignBookingSession(session.id.id)
    }
}
