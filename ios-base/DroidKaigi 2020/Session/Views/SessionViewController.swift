import ios_combined
import UIKit
import RxSwift
import RxCocoa
import MaterialComponents

final class SessionViewController: UIViewController {

    private let disposeBag = DisposeBag()

    @IBOutlet weak var filteredSessionCountLabel: UILabel! {
        didSet {
            filteredSessionCountLabel.font = ApplicationScheme.shared.typographyScheme.caption
        }
    }
    @IBOutlet weak var filterButton: MDCButton! {
        didSet {
            filterButton.isSelected = true
            filterButton.applyTextTheme(withScheme: ApplicationScheme.shared.buttonScheme)
            let filterListImage = UIImage(named: "ic_filter_list")
            let templateFilterListImage = filterListImage?.withRenderingMode(.alwaysTemplate)
            filterButton.setImage(templateFilterListImage, for: .selected)
            let arrowUpImage = UIImage(named: "ic_keyboard_arrow_up")
            let templateArrowUpImage = arrowUpImage?.withRenderingMode(.alwaysTemplate)
            filterButton.setImage(templateArrowUpImage, for: .normal)
            filterButton.setTitle("Filter", for: .selected)
            filterButton.setTitle("", for: .normal)
        }
    }
    @IBOutlet weak var collectionView: UICollectionView! {
        didSet {
            collectionView.register(UINib(nibName: "SessionCell", bundle: nil), forCellWithReuseIdentifier: SessionCell.identifier)
            let layout = UICollectionViewFlowLayout()
            layout.estimatedItemSize = .init(width: UIScreen.main.bounds.width, height: SessionCell.rowHeight)
            collectionView.collectionViewLayout = layout
        }
    }

    private let viewModel: SessionViewModel
    private let type: SessionViewControllerType

    init(viewModel: SessionViewModel, sessionViewType: SessionViewControllerType) {
        self.viewModel = viewModel
        self.type = sessionViewType
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        filterButton.rx.tap.asSignal()
            .emit(to: viewModel.toggleEmbddedView)
            .disposed(by: disposeBag)
        viewModel.isFocusedOnEmbeddedView
            .drive(filterButton.rx.isSelected)
            .disposed(by: disposeBag)

        /// TODO: Error handling for viewModel.sessions
        let dataSource = SessionViewDataSource()
        let filteredSessions = viewModel.sessions.asObservable()
            .map({ sessions -> [Session] in
                sessions.filter { Int($0.dayNumber) == self.type.rawValue }
            })
            .share(replay: 1, scope: .whileConnected)
        filteredSessions
            .bind(to: collectionView.rx.items(dataSource: dataSource))
            .disposed(by: disposeBag)
        filteredSessions
            .filter({ [weak self] sessions in
                guard let self = self else { return false }
                return sessions.count == 0 && self.type == .myPlan
            })
            .bind(to: Binder(self) { me, _ in
                DispatchQueue.main.async {
                    me.showSuggestView()
                }
            })
            .disposed(by: disposeBag)
    }

    func showSuggestView() {
        let suggestView = SuggestView()
        view.addSubview(suggestView)
        suggestView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            suggestView.topAnchor.constraint(equalTo: view.topAnchor),
            suggestView.leftAnchor.constraint(equalTo: view.leftAnchor),
            suggestView.rightAnchor.constraint(equalTo: view.rightAnchor),
            suggestView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }
}

