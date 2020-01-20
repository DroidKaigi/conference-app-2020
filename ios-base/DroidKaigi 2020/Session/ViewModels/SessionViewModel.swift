import ios_combined
import RxSwift
import RxCocoa

final class SessionViewModel {

    private let disposeBag = DisposeBag()

    // input
    let viewDidLoad = PublishRelay<Void>()
    let toggleEmbddedView = PublishRelay<Void>()

    // output
    let isFocusedOnEmbeddedView: Driver<Bool>
    let sessions: Driver<[Session]>

    init() {
        let isFocusedOnEmbeddedViewRelay = BehaviorRelay<Bool>(value: true)
        let sessionsRelay = BehaviorRelay<[Session]>(value: [])

        self.isFocusedOnEmbeddedView = isFocusedOnEmbeddedViewRelay.asDriver()
        self.sessions = sessionsRelay.asDriver()

        let dataProvider = SessionDataProvider()

        viewDidLoad.asObservable()
            .flatMap { dataProvider.fetchSessions() }
            .bind(to: sessionsRelay)
            .disposed(by: disposeBag)

        toggleEmbddedView
            .withLatestFrom(isFocusedOnEmbeddedViewRelay)
            .map { !$0 }
            .bind(to: isFocusedOnEmbeddedViewRelay)
            .disposed(by: disposeBag)
    }
}
