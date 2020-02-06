import ios_combined
import RxCocoa
import RxSwift

protocol FilterViewModelType {
    // Input
    func viewDidLoad()
    func selectChip()
    func resetSelected()
    func toggleEmbeddedView()

    // Output
    var sessionContents: Driver<SessionContents> { get }
    var isFocusedOnEmbeddedView: Driver<Bool> { get }
}

final class FilterViewModel: FilterViewModelType {
    private let disposeBag = DisposeBag()

    private let viewDidLoadRelay = PublishRelay<Void>()
    private let selectChipRelay = PublishRelay<Void>()
    private let resetSelectedRelay = PublishRelay<Void>()
    private let toggleEmbeddedViewRelay = PublishRelay<Void>()
    private let sessionContentsRelay = BehaviorRelay<SessionContents>(value: SessionContents.empty())
    let isFocusedOnEmbeddedViewRelay = BehaviorRelay<Bool>(value: true)

    var sessionContents: Driver<SessionContents>
    var isFocusedOnEmbeddedView: Driver<Bool>

    init(provider: SessionDataProvider = SessionDataProvider()) {
        sessionContents = sessionContentsRelay.asDriver()
        isFocusedOnEmbeddedView = isFocusedOnEmbeddedViewRelay.asDriver()

        viewDidLoadRelay
            .flatMap { provider.fetchSessionContents().asObservable() }
            .bind(to: sessionContentsRelay)
            .disposed(by: disposeBag)

        toggleEmbeddedViewRelay.asObservable()
            .withLatestFrom(isFocusedOnEmbeddedViewRelay)
            .map { !$0 }
            .bind(to: isFocusedOnEmbeddedViewRelay)
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

    func toggleEmbeddedView() {
        toggleEmbeddedViewRelay.accept(())
    }
}
