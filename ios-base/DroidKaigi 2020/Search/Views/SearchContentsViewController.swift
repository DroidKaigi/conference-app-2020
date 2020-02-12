import ioscombined
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
        layout.headerReferenceSize = .init(width: UIScreen.main.bounds.width, height: 60)
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
        collectionView.contentInset = .init(top: 16, left: 16, bottom: 0, right: 16)

        // register cells
        collectionView.register(UINib(nibName: "SearchSectionHeaderView", bundle: nil), forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: "SearchSectionHeaderView")
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

        dataSource.onTapSpeaker
            .emit(onNext: { [weak self] speaker, sessions in
                self?.navigationController?.pushViewController(
                    SpeakerViewController.instantiate(speaker: speaker, sessions: sessions), animated: true
                )
            })
            .disposed(by: disposeBag)

        dataSource.onTapBookmark
            .emit(to: Binder(self) { me, session in
                if session.isFavorited {
                    me.viewModel.resignBookingSession(session)
                } else {
                    me.viewModel.bookSession(session)
                }
            }).disposed(by: disposeBag)

        collectionView.rx.itemSelected.asObservable()
            .bind(to: Binder(self) { me, indexPath in
                do {
                    let model = try dataSource.model(at: indexPath)
                    switch model {
                    case let speaker as Speaker:
                        // FIXME: - SpeakerView only shows empty sessions.
                        me.navigationController?.pushViewController(
                            SpeakerViewController.instantiate(speaker: speaker, sessions: []), animated: true
                        )
                    case let session as Session:
                        me.showDetail(forSession: session)
                    default:
                        fatalError()
                    }
                } catch {
                    return
                }
            }).disposed(by: disposeBag)
    }
}

extension SearchContentsViewController: SearchBarDelegate {
    func prepareSearchBar() {
        guard let searchBar = searchBarController?.searchBar else {
            return
        }
        searchBar.delegate = self
        searchBar.textField.delegate = self
    }

    func searchBar(searchBar: SearchBar, didClear textField: UITextField, with text: String?) {
        viewModel.clear()
    }

    func searchBar(searchBar: SearchBar, didChange textField: UITextField, with text: String?) {
        let text = text ?? ""
        viewModel.search(query: text)
    }
}

extension SearchContentsViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}
