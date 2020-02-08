import ios_combined
import RxCocoa
import RxSwift

protocol SearchViewModelType {
    // Input
    func search(query: String)
    func clear()

    // Output
    var searchResult: Driver<SearchResult> { get }
}

final class SearchViewModel: SearchViewModelType {
    // Input
    private let searchRelay = PublishRelay<String>()
    private let clearRelay = PublishRelay<Void>()

    // Output
    private let searchResultRelay = BehaviorRelay<SearchResult>(value: .empty())
    private let sessionContentsRelay = BehaviorRelay<SessionContents>(value: .empty())

    private let disposeBag = DisposeBag()

    var searchResult: Driver<SearchResult>

    init() {
        searchResult = searchResultRelay.asDriver()

        let dataProvider = SessionDataProvider()
        dataProvider.fetchSessionContents()
            .subscribe(onSuccess: { [weak self] sessionContents in
                self?.sessionContentsRelay.accept(sessionContents)
                self?.searchResultRelay.accept(sessionContents.search(query: ""))
            }, onError: nil)
            .disposed(by: disposeBag)

        searchRelay
            .withLatestFrom(sessionContentsRelay) { ($0, $1) }
            .map { args -> SearchResult in
                let (query, sessionContents) = args
                return sessionContents.search(query: query)
            }
            .bind(to: searchResultRelay)
            .disposed(by: disposeBag)

        clearRelay.asObservable()
            .withLatestFrom(sessionContentsRelay)
            .map { $0.search(query: "") }
            .bind(to: searchResultRelay)
            .disposed(by: disposeBag)
    }

    func search(query: String) {
        searchRelay.accept(query)
    }

    func clear() {
        clearRelay.accept(())
    }
}
