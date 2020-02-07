import ios_combined
import RxCocoa
import RxSwift

protocol FilterViewModelType {
    // Input
    func viewDidLoad()
    func selectChip(chip: Any)
    func resetSelected()
    func toggleEmbeddedView()
    func turnBackEmbeddedView()

    // Output
    var sessionContents: Driver<FilterSessionContents> { get }
    var selectedSessionContents: Driver<FilterSessionContents> { get }
    var isFocusedOnEmbeddedView: Driver<Bool> { get }
}

final class FilterViewModel: FilterViewModelType {
    private let disposeBag = DisposeBag()

    private let viewDidLoadRelay = PublishRelay<Void>()
    private let selectChipRelay = PublishRelay<Any>()
    private let resetSelectedRelay = PublishRelay<Void>()
    private let toggleEmbeddedViewRelay = PublishRelay<Void>()
    private let turnBackEmbeddedViewRelay = PublishRelay<Void>()
    private let sessionContentsRelay = BehaviorRelay<FilterSessionContents>(value: FilterSessionContents.empty())
    private let selectedSessionContentsRelay = BehaviorRelay<FilterSessionContents>(value: FilterSessionContents.empty())
    let isFocusedOnEmbeddedViewRelay = BehaviorRelay<Bool>(value: true)

    var sessionContents: Driver<FilterSessionContents>
    var selectedSessionContents: Driver<FilterSessionContents>
    var isFocusedOnEmbeddedView: Driver<Bool>

    init(provider: SessionDataProvider = SessionDataProvider()) {
        sessionContents = sessionContentsRelay.asDriver()
        selectedSessionContents = selectedSessionContentsRelay.asDriver()
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

        selectChipRelay.asObservable()
            .withLatestFrom(selectedSessionContentsRelay) { ($0, $1) }
            .subscribe(onNext: { [weak self] args in
                var (chip, sessionContents) = args
                switch chip {
                case let room as Room:
                    sessionContents.rooms.append(room)
                case let lang as Lang:
                    sessionContents.langs.append(lang)
                case let level as Level:
                    sessionContents.levels.append(level)
                case let category as ios_combined.Category:
                    sessionContents.categories.append(category)
                case let langSupport as LangSupport:
                    sessionContents.langSupports.append(langSupport)
                default:
                    break
                }
                self?.selectedSessionContentsRelay.accept(sessionContents)
            }).disposed(by: disposeBag)
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
