import Material
import UIKit

final class SearchContentsViewController: UIViewController {
    var collectionView: CollectionView!

    let viewModel = SearchViewModel()

    override func viewDidLoad() {
        super.viewDidLoad()
        setUpCollectionView()
        bindViewModel()
    }

    private func setUpCollectionView() {
        let layout = UICollectionViewFlowLayout()
        layout.estimatedItemSize = .init(width: view.bounds.width, height: ResultSpeakerCell.rowHeight)
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

        // register cells
        collectionView.register(UINib(nibName: "SessionCell", bundle: nil), forCellWithReuseIdentifier: SessionCell.identifier)
        collectionView.register(UINib(nibName: "ResultSpeakerCell", bundle: nil), forCellWithReuseIdentifier: ResultSpeakerCell.identifier)
    }

    private func bindViewModel() {

    }
}
