import ios_combined
import RxCocoa
import RxSwift
import SafariServices
import UIKit

final class SponsorViewController: UIViewController {
    private let disposeBag = DisposeBag()

    @IBOutlet private var collectionView: UICollectionView! {
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

    private lazy var loadingIndicatorView: UIActivityIndicatorView = {
        let indicator = UIActivityIndicatorView(style: .gray)
        return indicator
    }()

    private lazy var retryButton: UIButton = {
        let button = UIButton()
        if LangKt.defaultLang() == .ja {
            button.setTitle("リトライ", for: .normal)
        } else {
            button.setTitle("Retry", for: .normal)
        }
        button.sizeToFit()
        button.rx.tap
            .bind(to: Binder(self) { me, _ in
                me.viewModel.retry()
            })
            .disposed(by: disposeBag)
        return button
    }()

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
            .bind(to: Binder(self) { me, sponsor in
                me.showCompanyWebSite(for: sponsor)
            })
            .disposed(by: disposeBag)

        Driver.combineLatest(viewModel.isLoading, viewModel.error)
            .drive(Binder(self) { me, element in
                me.updateBackgroudView(isLoading: element.0, error: element.1)
            })
            .disposed(by: disposeBag)

        viewModel.viewDidLoad()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.navigationBar.barTintColor = ApplicationScheme.shared.colorScheme.surfaceColor
        navigationController?.navigationBar.tintColor = ApplicationScheme.shared.colorScheme.onSurfaceColor
        navigationController?.navigationBar.shadowImage = UIImage()
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
            navigationItem.title = "Sponsors"
        }

        menuItem.rx.tap
            .bind(to: Binder(self) { me, _ in
                me.navigationDrawerController?.toggleLeftView()
            })
            .disposed(by: disposeBag)
    }

    private func updateBackgroudView(isLoading: Bool, error: KotlinError?) {
        if isLoading {
            collectionView.backgroundView = loadingIndicatorView
            loadingIndicatorView.startAnimating()
        } else {
            if error != nil {
                collectionView.backgroundView = retryButton
            } else {
                collectionView.backgroundView = nil
            }
        }
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
