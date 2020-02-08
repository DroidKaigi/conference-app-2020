import ios_combined
import RxCocoa
import RxSwift

protocol SearchViewModelType {
    // Input
    func search(query: String)

    // Output
    var searchResult: Driver<SearchResult> { get }
}

final class SearchViewModel: SearchViewModelType {
    // Input
    private let searchRelay = PublishRelay<String>()

    // Output
    private let searchResultRelay = BehaviorRelay<SearchResult>(value: .empty())

    var searchResult: Driver<SearchResult>

    init() {
        searchResult = searchResultRelay.asDriver()
    }

    func search(query: String) {
        searchRelay.accept(query)
    }
}
