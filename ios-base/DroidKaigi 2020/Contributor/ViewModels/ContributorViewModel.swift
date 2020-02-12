import ioscombined
import RxCocoa
import RxSwift

protocol ContributorViewModelType {
    // input
    func viewDidLoad()
    func retry()

    // output
    var contributorIndices: Driver<[ContributorIndex]> { get }
    var isLoading: Driver<Bool> { get }
    var error: Driver<KotlinError?> { get }
}

final class ContributorViewModel: ContributorViewModelType {
    private let disposeBag = DisposeBag()

    // input
    private let viewDidLoadRelay = PublishRelay<Void>()
    private let retryRelay = PublishRelay<Void>()

    func viewDidLoad() {
        viewDidLoadRelay.accept(())
    }

    func retry() {
        retryRelay.accept(())
    }

    // output
    let contributorIndices: Driver<[ContributorIndex]>
    let isLoading: Driver<Bool>
    let error: Driver<KotlinError?>

    init(
        dataProvider: ContributorDataProviderProtocol = ContributorDataProvider()
    ) {
        let contributorIndicesRelay = BehaviorRelay<[ContributorIndex]>(value: [])
        let isLoadingRelay = BehaviorRelay<Bool>(value: false)
        let errorRelay = BehaviorRelay<KotlinError?>(value: nil)

        contributorIndices = contributorIndicesRelay.asDriver()
        isLoading = isLoadingRelay.asDriver()
        error = errorRelay.asDriver()

        let fetchResult = Observable.merge(viewDidLoadRelay.asObservable(), retryRelay.asObservable())
            .do(onNext: { isLoadingRelay.accept(true) })
            .flatMap { dataProvider.fetchContributors().asObservable().materialize() }
            .do(onNext: { _ in isLoadingRelay.accept(false) })
            .share()

        fetchResult.compactMap { $0.element }
            .bind(to: contributorIndicesRelay)
            .disposed(by: disposeBag)

        fetchResult.map { $0.error as? KotlinError }
            .bind(to: errorRelay)
            .disposed(by: disposeBag)
    }
}
