import ios_combined
import RxCocoa
import RxSwift

final class ContributorViewModel {
    private let disposeBag = DisposeBag()

    // input
    private let viewDidLoadRelay = PublishRelay<Void>()

    func viewDidLoad() {
        viewDidLoadRelay.accept(())
    }

    // output
    let contributorIndices: Driver<[ContributorIndex]>
    let isLoading: Driver<Bool>

    init() {
        let contributorIndicesRelay = BehaviorRelay<[ContributorIndex]>(value: [])
        let isLoadingRelay = BehaviorRelay<Bool>(value: false)

        contributorIndices = contributorIndicesRelay.asDriver()
        isLoading = isLoadingRelay.asDriver()

        let dataProvider = ContributorDataProvider()

        let fetchResult = viewDidLoadRelay.asObservable()
            .do(onNext: { isLoadingRelay.accept(true) })
            .flatMap { dataProvider.fetchContributors().asObservable() }
            .do(onNext: { _ in isLoadingRelay.accept(false) })

        fetchResult
            .bind(to: contributorIndicesRelay)
            .disposed(by: disposeBag)
    }
}
