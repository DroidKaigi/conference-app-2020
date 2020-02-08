import ios_combined
import Material
import RxCocoa
import RxSwift
import UIKit

final class SearchContentsViewController: UIViewController {
    private let disposeBag = DisposeBag()

    var collectionView: CollectionView!

    let viewModel = SearchViewModel()

    override func viewDidLoad() {
        super.viewDidLoad()
        setUpCollectionView()
        prepareSearchBar()
        bindViewModel()
    }

    private func setUpCollectionView() {
        let layout = CollectionViewFlowLayoutLeftAlign()
        layout.estimatedItemSize = .init(width: UIScreen.main.bounds.width, height: ResultSpeakerCell.rowHeight)
        layout.minimumLineSpacing = 16
        collectionView = CollectionView(collectionViewLayout: layout)
        view.addSubview(collectionView)
        collectionView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            collectionView.topAnchor.constraint(equalTo: view.topAnchor),
            collectionView.leftAnchor.constraint(equalTo: view.leftAnchor),
            collectionView.rightAnchor.constraint(equalTo: view.rightAnchor),
            collectionView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
        collectionView.contentInset = .init(top: 16, left: 0, bottom: 0, right: 0)

        // register cells
        collectionView.register(UINib(nibName: "SessionCell", bundle: nil), forCellWithReuseIdentifier: SessionCell.identifier)
        collectionView.register(UINib(nibName: "ResultSpeakerCell", bundle: nil), forCellWithReuseIdentifier: ResultSpeakerCell.identifier)
    }

    private func bindViewModel() {
        let dataSource = SearchContentsDataSource()
        viewModel.searchResult
            .map { (searchResult) -> SearchResult in
                let sessions = searchResult.sessions.sorted(by: { (s1, s2) -> Bool in
                    s1.title.currentLangString < s2.title.currentLangString
                })
                let speakers = searchResult.speakers.sorted(by: { (s1, s2) -> Bool in
                    s1.name < s2.name
                })
                return SearchResult(sessions: sessions, speakers: speakers, query: "")
            }
            .drive(collectionView.rx.items(dataSource: dataSource))
            .disposed(by: disposeBag)
    }
}

extension SearchContentsViewController: SearchBarDelegate {
    func prepareSearchBar() {
        guard let searchBar = searchBarController?.searchBar else {
            return
        }
        searchBar.delegate = self
    }

    func searchBar(searchBar: SearchBar, didClear textField: UITextField, with text: String?) {
        viewModel.clear()
    }

    func searchBar(searchBar: SearchBar, didChange textField: UITextField, with text: String?) {
        let text = text ?? ""
        viewModel.search(query: text)
    }
}
