import ios_combined
import RxCocoa
import RxSwift
import UIKit

final class AnnouncementsViewController: UIViewController {
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            tableView.tableFooterView = UIView()
            tableView.separatorStyle = .none
            tableView.allowsSelection = false
            tableView.refreshControl = refreshControl
            tableView.register(UINib(nibName: String(describing: AnnouncementCell.self), bundle: .none), forCellReuseIdentifier: String(describing: AnnouncementCell.self))
        }
    }

    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }

    private let refreshControl = UIRefreshControl()
    private let viewModel: AnnouncementsViewModelType
    private let disposeBag = DisposeBag()

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    init(viewModel: AnnouncementsViewModelType) {
        self.viewModel = viewModel

        super.init(nibName: nil, bundle: nil)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationController?.navigationBar.titleTextAttributes = [.foregroundColor: UIColor.black]
        navigationController?.navigationBar.barTintColor = ApplicationScheme.shared.colorScheme.backgroundColor
        navigationController?.navigationBar.tintColor = ApplicationScheme.shared.colorScheme.onBackgroundColor

        let templateMenuImage = Asset.icMenu.image.withRenderingMode(.alwaysTemplate)
        let menuItem = UIBarButtonItem(image: templateMenuImage,
                                       style: .plain,
                                       target: self,
                                       action: nil)
        let titleItem = UIBarButtonItem(title: L10n.announcements,
                                        style: .plain,
                                        target: nil,
                                        action: nil)
        titleItem.setTitleTextAttributes([.foregroundColor: UIColor.black], for: .disabled)
        titleItem.isEnabled = false
        navigationItem.leftBarButtonItems = [menuItem, titleItem]

        menuItem.rx.tap
            .bind(to: Binder(self) { target, _ in
                target.navigationDrawerController?.toggleLeftView()
            })
            .disposed(by: disposeBag)

        let dataSource = AnnouncementsDataSource()

        refreshControl.rx.controlEvent(.valueChanged)
            .bind(to: Binder(self) { target, _ in
                target.refreshControl.beginRefreshing()
                target.viewModel.pullToRefresh()
            })
            .disposed(by: disposeBag)

        viewModel.announcements
            .map { _ in }
            .drive(Binder(self) { target, _ in
                target.refreshControl.endRefreshing()
            })
            .disposed(by: disposeBag)

        viewModel.announcements
            .drive(tableView.rx.items(dataSource: dataSource))
            .disposed(by: disposeBag)

        viewModel.viewDidLoad()
    }
}

extension AnnouncementsViewController {
    static func instantiate() -> AnnouncementsViewController {
        AnnouncementsViewController(viewModel: AnnouncementsViewModel(provider: AnnouncementsDataProvider()))
    }
}
