import ioscombined
import RxCocoa
import RxSwift

final class SponsorViewModel {
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
    let sponsorCategories: Driver<[SponsorCategory]>
    let isLoading: Driver<Bool>
    let error: Driver<KotlinError?>

    init() {
        let sponsorCategoriesRelay = BehaviorRelay<[SponsorCategory]>(value: [])
        let isLoadingRelay = BehaviorRelay<Bool>(value: false)
        let errorRelay = BehaviorRelay<KotlinError?>(value: nil)

        sponsorCategories = sponsorCategoriesRelay.asDriver()
        isLoading = isLoadingRelay.asDriver()
        error = errorRelay.asDriver()

        let dataProvider = SponsorDataProvider()

        let fetchResult = Observable.merge(viewDidLoadRelay.asObservable(), retryRelay.asObservable())
            .do(onNext: { isLoadingRelay.accept(true) })
            .flatMap { dataProvider.fetchSponsors().asObservable().materialize() }
            .do(onNext: { _ in isLoadingRelay.accept(false) })
            .share()

        fetchResult.compactMap { $0.element }
            .bind(to: sponsorCategoriesRelay)
            .disposed(by: disposeBag)

        fetchResult.map { $0.error as? KotlinError }
            .bind(to: errorRelay)
            .disposed(by: disposeBag)
    }
}
