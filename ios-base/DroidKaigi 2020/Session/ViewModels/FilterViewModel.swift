import ios_combined
import RxCocoa
import RxSwift

protocol FilterViewModelType {
    // Input
    func viewDidLoad()
    func selectChip()
    func resetSelected()
    func toggleEmbeddedView()
    func turnBackEmbeddedView()

    // Output
    var sessionContents: Driver<FilterSessionContents> { get }
    var isFocusedOnEmbeddedView: Driver<Bool> { get }
}

final class FilterViewModel: FilterViewModelType {
    private let disposeBag = DisposeBag()

    private let viewDidLoadRelay = PublishRelay<Void>()
    private let selectChipRelay = PublishRelay<Void>()
    private let resetSelectedRelay = PublishRelay<Void>()
    private let toggleEmbeddedViewRelay = PublishRelay<Void>()
    private let turnBackEmbeddedViewRelay = PublishRelay<Void>()
    private let sessionContentsRelay = BehaviorRelay<FilterSessionContents>(value: FilterSessionContents.empty())
    let isFocusedOnEmbeddedViewRelay = BehaviorRelay<Bool>(value: true)

    var sessionContents: Driver<FilterSessionContents>
    var isFocusedOnEmbeddedView: Driver<Bool>

    init(provider: SessionDataProvider = SessionDataProvider()) {
        sessionContents = sessionContentsRelay.asDriver()
        isFocusedOnEmbeddedView = isFocusedOnEmbeddedViewRelay.asDriver()

        viewDidLoadRelay
            .flatMap { provider.fetchSessionContents().asObservable() }
            .map(FilterSessionContents.init)
            .bind(to: sessionContentsRelay)
            .disposed(by: disposeBag)

        toggleEmbeddedViewRelay.asObservable()
            .withLatestFrom(isFocusedOnEmbeddedViewRelay)
            .map { !$0 }
            .bind(to: isFocusedOnEmbeddedViewRelay)
            .disposed(by: disposeBag)

        turnBackEmbeddedViewRelay.asObservable()
            .withLatestFrom(isFocusedOnEmbeddedViewRelay)
            .bind(to: isFocusedOnEmbeddedViewRelay)
            .disposed(by: disposeBag)
    }

    func viewDidLoad() {
        viewDidLoadRelay.accept(())
    }

    func selectChip(chip: Any) {
        selectChipRelay.accept(chip)
    }

    func resetSelected() {
        resetSelectedRelay.accept(())
    }

    func toggleEmbeddedView() {
        toggleEmbeddedViewRelay.accept(())
    }

    func turnBackEmbeddedView() {
        turnBackEmbeddedViewRelay.accept(())
    }
}
