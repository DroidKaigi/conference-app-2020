import Material
import UIKit

final class SidebarViewController: UITableViewController {
    enum SwitchViewControllerType: Int {
        case timeline
        case about
        case info
        case map
        case sponsor
        case contributor
        case setting
    }

    @IBOutlet weak var timelineLabel: UILabel! {
        didSet {
            timelineLabel.text = L10n.timeline
        }
    }

    @IBOutlet weak var aboutLabel: UILabel! {
        didSet {
            aboutLabel.text = L10n.about
        }
    }

    @IBOutlet weak var infoLabel: UILabel! {
        didSet {
            infoLabel.text = L10n.announcements
        }
    }

    @IBOutlet weak var floorMapLabel: UILabel! {
        didSet {
            floorMapLabel.text = L10n.floormap
        }
    }

    @IBOutlet weak var sponsorLabel: UILabel! {
        didSet {
            sponsorLabel.text = L10n.sponsor
        }
    }

    @IBOutlet weak var settingLabel: UILabel! {
        didSet {
            settingLabel.text = L10n.setting
        }
    }

    weak var rootViewController: UINavigationController?

    var switchType: SwitchViewControllerType?

    struct Dependency {
        let switchType: SwitchViewControllerType
    }

    func inject(with dependency: Dependency) {
        switchType = dependency.switchType
    }

    static func instantiate(rootViewController: UINavigationController, dependency: Dependency = .init(switchType: .timeline)) -> SidebarViewController {
        guard let viewController = UIStoryboard(name: "SidebarViewController", bundle: .main).instantiateInitialViewController() as? SidebarViewController else { fatalError() }
        viewController.inject(with: dependency)
        viewController.rootViewController = rootViewController
        return viewController
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.tableHeaderView?.frame.size.height = 190
        tableView.rowHeight = 65
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        // select default cell (timeline)
        let indexPath = IndexPath(row: switchType?.rawValue ?? SwitchViewControllerType.timeline.rawValue, section: 0)
        tableView.selectRow(at: indexPath, animated: false, scrollPosition: .none)
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let cell = tableView.cellForRow(at: indexPath),
            let switchType = SwitchViewControllerType(rawValue: cell.tag),
            let rootViewController = rootViewController else {
            return
        }

        switch switchType {
        case .timeline:
            if rootViewController.viewControllers.first is FilterViewController {
                break
            }
            let filterViewController = FilterViewController()
            transition(to: filterViewController)
        case .about:
            if rootViewController.viewControllers.first is AboutViewController {
                break
            }
            let aboutViewController = AboutViewController.instantiate()
            transition(to: aboutViewController)
        case .info:
            let controller = AnnouncementsViewController.instantiate()
            transition(to: controller)
        case .map:
            if rootViewController.viewControllers.first is FloorMapViewController {
                break
            }
            let floorMapViewController = FloorMapViewController.instantiate()
            transition(to: floorMapViewController)

        case .sponsor:
            if rootViewController.viewControllers.first is SponsorViewController {
                break
            }
            let vc = SponsorViewController()
            transition(to: vc)
        case .contributor:
            if rootViewController.viewControllers.first is ContributorViewController {
                break
            }
            let contributorViewController = ContributorViewController.instantiate()
            rootViewController.pushViewController(contributorViewController, animated: true)
        case .setting:
            break
        }

        rootViewController.navigationDrawerController?.toggleLeftView()
    }

    private func transition(to viewController: UIViewController) {
        guard let navigationDrawerController = self.navigationDrawerController else { return }
        let navigationController = NavigationController(rootViewController: viewController)
        navigationDrawerController.transition(to: navigationController) { [weak self] isFinishing in
            guard let self = self, isFinishing else { return }
            self.rootViewController = navigationController
        }
        navigationDrawerController.toggleLeftView()
    }
}
