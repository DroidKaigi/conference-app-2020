import ioscombined
import RxCocoa
import RxSwift
import SafariServices
import UIKit

final class ContributorViewController: ContentViewController {
    private let disposeBag = DisposeBag()

    @IBOutlet weak var stackView: UIStackView!
    @IBOutlet weak var loadingView: UIView!
    @IBOutlet weak var errorView: UIView!

    @IBOutlet weak var collectionView: UICollectionView! {
        didSet {
            collectionView.register(
                UINib(nibName: ContributorCell.identifier, bundle: nil),
                forCellWithReuseIdentifier: ContributorCell.identifier
            )

            collectionView.register(
                UINib(nibName: ContributorIndexHeaderView.identifier, bundle: nil),
                forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader,
                withReuseIdentifier: ContributorIndexHeaderView.identifier
            )

            let layout = UICollectionViewFlowLayout()
            layout.itemSize = CGSize(width: UIScreen.main.bounds.width, height: ContributorCell.rowHeight)
            layout.headerReferenceSize = CGSize(width: UIScreen.main.bounds.width, height: .leastNormalMagnitude)
            layout.minimumLineSpacing = .zero
            collectionView.collectionViewLayout = layout

            collectionView.rx.modelSelected(Contributor.self)
                .bind(to: Binder(self) { me, contributor in
                    me.openProfileURL(for: contributor)
                })
                .disposed(by: disposeBag)
        }
    }

    @IBOutlet private weak var retryButton: UIButton! {
        didSet {
            retryButton.rx.tap
                .bind(to: Binder(self) { me, _ in
                    me.viewModel.retry()
                })
                .disposed(by: disposeBag)
        }
    }

    private let viewModel = ContributorViewModel()

    override func viewDidLoad() {
        super.viewDidLoad()

        let dataSource = ContributorViewDataSource()

        viewModel.contributorIndices.asObservable()
            .bind(to: collectionView.rx.items(dataSource: dataSource))
            .disposed(by: disposeBag)

        Driver.combineLatest(viewModel.isLoading, viewModel.error)
            .drive(Binder(self) { me, element in
                me.updateContentView(isLoading: element.0, error: element.1)
            })
            .disposed(by: disposeBag)

        viewModel.viewDidLoad()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.navigationBar.barTintColor = ApplicationScheme.shared.colorScheme.surfaceColor
        navigationController?.navigationBar.tintColor = ApplicationScheme.shared.colorScheme.onSurfaceColor
    }

    private func updateContentView(isLoading: Bool, error: KotlinError?) {
        stackView.arrangedSubviews.forEach { $0.isHidden = true }

        if isLoading {
            loadingView.isHidden = false
        } else if error != nil {
            errorView.isHidden = false
        } else {
            collectionView.isHidden = false
        }
    }

    private func openProfileURL(for contributor: Contributor) {
        guard let url = URL(string: contributor.profileUrl) else {
            return
        }
        let viewController = SFSafariViewController(url: url)
        present(viewController, animated: true)
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
