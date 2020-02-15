import ioscombined
import RxCocoa
import RxSwift
import SafariServices
import UIKit

final class SponsorViewController: ContentViewController {
    private let disposeBag = DisposeBag()

    @IBOutlet private weak var collectionView: UICollectionView! {
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

    init() {
        super.init(nibName: nil, bundle: nil)
        title = L10n.sponsor
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    @IBOutlet private weak var loadingIndicatorView: UIActivityIndicatorView!

    @IBOutlet private weak var retryButton: UIButton! {
        didSet {
            if LangKt.defaultLang() == .ja {
                retryButton.setTitle("リトライ", for: .normal)
            } else {
                retryButton.setTitle("Retry", for: .normal)
            }

            retryButton.rx.tap
                .bind(to: Binder(self) { me, _ in
                    me.viewModel.retry()
                })
                .disposed(by: disposeBag)
        }
    }

    private let viewModel = SponsorViewModel()

    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .default
    }

    override func viewDidLoad() {
        super.viewDidLoad()

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
    }

    private func updateBackgroudView(isLoading: Bool, error: KotlinError?) {
        if isLoading {
            loadingIndicatorView.startAnimating()
        } else {
            loadingIndicatorView.stopAnimating()

            if error != nil {
                retryButton.isHidden = false
            } else {
                retryButton.isHidden = true
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
