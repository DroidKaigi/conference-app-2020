import Foundation
import ios_combined
import RxCocoa
import RxSwift

final class AnnouncementsViewModel {
    let announcements: Driver<[Announcement]>
    let error: Driver<KotlinError?>

    private let viewDidLoadReplay = PublishRelay<Void>()
    private let pullToRefreshRelay = PublishRelay<Void>()

    private let disposeBag = DisposeBag()

    init() {
        let announcementsRelay = BehaviorRelay<[Announcement]>(value: [])
        let errorRelay = BehaviorRelay<KotlinError?>(value: nil)

        announcements = announcementsRelay.asDriver()
        error = errorRelay.asDriver()

        let provider = AnnouncementsDataProvider()

        let fetchResult = Observable.merge(viewDidLoadReplay.asObservable(), pullToRefreshRelay.asObservable())
            .flatMap { provider.fetch().asObservable().materialize() }
            .share()

        fetchResult.map { $0.element }
            .filterNil()
            .bind(to: announcementsRelay)
            .disposed(by: disposeBag)
        fetchResult.map { $0.error as? KotlinError }
            .bind(to: errorRelay)
            .disposed(by: disposeBag)
    }

    func viewDidLoad() {
        viewDidLoadReplay.accept(())
    }

    func pullToRefresh() {
        pullToRefreshRelay.accept(())
    }
}
