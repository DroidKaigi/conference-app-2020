import ios_combined
import RxCocoa
import RxSwift

protocol FilterViewModelType {
    // Input
    func viewDidLoad()
    func selectChip()
    func resetSelected()

    // Output
    var sessionContents: Driver<SessionContents> { get }
}

final class FilterViewModel: FilterViewModelType {

    private let disposeBag = DisposeBag()

    private let viewDidLoadRelay = PublishRelay<Void>()
    private let selectChipRelay = PublishRelay<Void>()
    private let resetSelectedRelay = PublishRelay<Void>()
    private let sessionContentsRelay = BehaviorRelay<SessionContents>(value: SessionContents.empty())

    var sessionContents: Driver<SessionContents>

    init(provider: SessionDataProvider = SessionDataProvider()) {
        self.sessionContents = sessionContentsRelay.asDriver()

        viewDidLoadRelay
            .flatMap { provider.fetchSessionContents().asObservable() }
            .bind(to: sessionContentsRelay)
            .disposed(by: disposeBag)
    }

    func viewDidLoad() {
        viewDidLoadRelay.accept(())
    }

    func selectChip() {
        selectChipRelay.accept(())
    }

    func resetSelected() {
        resetSelectedRelay.accept(())
    }
}
