import ios_combined
import RxCocoa
import RxSwift
import SafariServices
import UIKit

final class SponsorViewController: UIViewController {
    private let disposeBag = DisposeBag()

    @IBOutlet weak var collectionView: UICollectionView! {
        didSet {
            collectionView.register(
                UINib(nibName: "SponsorCell", bundle: nil),
                forCellWithReuseIdentifier: SponsorCell.identifier
            )
            collectionView.register(
                UINib(nibName: "SponsorCategoryHeaderView", bundle: nil),
                forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader,
                withReuseIdentifier: SponsorCategoryHeaderView.identifier
            )
            collectionView.register(
                UINib(nibName: "SponsorDividerView", bundle: nil),
                forSupplementaryViewOfKind: UICollectionView.elementKindSectionFooter,
                withReuseIdentifier: SponsorDividerView.identifier
            )
        }
    }

    private let viewModel = SponsorViewModel()

    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .default
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        setUpAppBar()

        let dataSource = SponsorViewDataSource()
        viewModel.sponsorCategories.asObservable()
            .bind(to: collectionView.rx.items(dataSource: dataSource))
            .disposed(by: disposeBag)

        collectionView.rx.setDelegate(dataSource)
            .disposed(by: disposeBag)

        collectionView.rx.modelSelected(Sponsor.self)
            .subscribe(onNext: { [weak self] sponsor in
                self?.showCompanyWebSite(for: sponsor)
            })
            .disposed(by: disposeBag)

        // rx
        viewModel.viewDidLoad()
    }

    private func setUpAppBar() {
        let menuImage = UIImage(named: "ic_menu")
        let templateMenuImage = menuImage?.withRenderingMode(.alwaysTemplate)
        let menuItem = UIBarButtonItem(image: templateMenuImage,
                                       style: .plain,
                                       target: self,
                                       action: nil)
        navigationItem.leftBarButtonItems = [menuItem]
        if LangKt.defaultLang() == .ja {
            navigationItem.title = "スポンサー"
        } else {
            navigationItem.title = "Sponsor"
        }
        navigationController?.view.backgroundColor = ApplicationScheme.shared.colorScheme.surfaceColor
        navigationController?.navigationBar.tintColor = ApplicationScheme.shared.colorScheme.onSurfaceColor
        navigationController?.navigationBar.backgroundColor = ApplicationScheme.shared.colorScheme.surfaceColor
        navigationController?.navigationBar
            .setBackgroundImage(UIImage(), for: .default)
        navigationController?.navigationBar.shadowImage = UIImage()
        edgesForExtendedLayout = []
    }

    private func showCompanyWebSite(for sponsor: Sponsor) {
        guard let url = URL(string: sponsor.company.url) else {
            // Do nothing if the URL is invalid.
            return
        }
        let vc = SFSafariViewController(url: url)
        present(vc, animated: true, completion: nil)
    }
}
