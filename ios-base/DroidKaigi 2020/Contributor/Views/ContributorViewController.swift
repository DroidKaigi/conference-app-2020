import RxCocoa
import RxSwift
import UIKit

final class ContributorViewController: UIViewController {
    private let disposeBag = DisposeBag()

    @IBOutlet weak var collectionView: UICollectionView! {
        didSet {
            collectionView.register(
                UINib(nibName: ContributorCell.identifier, bundle: nil),
                forCellWithReuseIdentifier: ContributorCell.identifier
            )

            let layout = UICollectionViewFlowLayout()
            layout.itemSize = CGSize(width: UIScreen.main.bounds.width, height: ContributorCell.rowHeight)
            collectionView.collectionViewLayout = layout
        }
    }
    
    private let viewModel = ContributorViewModel()

    override func viewDidLoad() {
        super.viewDidLoad()

        let dataSource = ContributorViewDataSource()

        viewModel.contributorIndices.asObservable()
            .bind(to: collectionView.rx.items(dataSource: dataSource))
            .disposed(by: disposeBag)

        viewModel.viewDidLoad()        
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.navigationBar.barTintColor = ApplicationScheme.shared.colorScheme.surfaceColor
        navigationController?.navigationBar.tintColor = ApplicationScheme.shared.colorScheme.onSurfaceColor
    }
}

extension ContributorViewController {
    static func instantiate() -> ContributorViewController {
        let storyboard = UIStoryboard(name: String(describing: self), bundle: .main)
        guard let viewController = storyboard.instantiateInitialViewController() as? ContributorViewController else {
            fatalError()
        }
        return viewController
    }
}
