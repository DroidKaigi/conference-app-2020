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

    init() {
        let contributorIndicesRelay = BehaviorRelay<[ContributorIndex]>(value: [])

        contributorIndices = contributorIndicesRelay.asDriver()

        let dataProvider = ContributorDataProvider()

        let fetchResult = viewDidLoadRelay.asObservable()
            .flatMap { dataProvider.fetchContributors().asObservable() }

        fetchResult
            .bind(to: contributorIndicesRelay)
            .disposed(by: disposeBag)
    }
}
