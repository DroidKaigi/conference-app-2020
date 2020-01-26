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

    private let refreshControl = UIRefreshControl()

    private let viewModel: AnnouncementsViewModel

    private let disposeBag = DisposeBag()

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    init(viewModel: AnnouncementsViewModel) {
        self.viewModel = viewModel

        super.init(nibName: nil, bundle: nil)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

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
        AnnouncementsViewController(viewModel: AnnouncementsViewModel())
    }
}
