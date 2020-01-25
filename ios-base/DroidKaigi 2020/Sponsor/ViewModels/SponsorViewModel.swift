import ios_combined
import RxCocoa
import RxSwift

final class SponsorViewModel {
    private let disposeBag = DisposeBag()

    // input
    private let viewDidLoadRelay = PublishRelay<Void>()

    func viewDidLoad() {
        viewDidLoadRelay.accept(())
    }

    // output
    let sponsorCategories: Driver<[SponsorCategory]>

    init() {
        let sponsorCategoriesRelay = BehaviorRelay<[SponsorCategory]>(value: [])

        sponsorCategories = sponsorCategoriesRelay.asDriver()

        let dataProvider = SponsorDataProvider()

        viewDidLoadRelay.asObservable()
            .flatMap { dataProvider.fetchSponsors() }
            .bind(to: sponsorCategoriesRelay)
            .disposed(by: disposeBag)
    }
}
