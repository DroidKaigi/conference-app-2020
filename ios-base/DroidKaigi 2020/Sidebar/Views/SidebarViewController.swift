import Material
import SafariServices
import UIKit

final class SidebarViewController: UITableViewController {
    enum SwitchViewControllerType: Int {
        case timeline = 0
        case about = 1
        case info = 2
        case map = 3
        case sponsor = 4
        case contributor = 5
        case setting = 6
        case survey = 7
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

    @IBOutlet weak var entireSurveyLabel: UILabel! {
        didSet {
            entireSurveyLabel.text = L10n.survey
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
            transition(to: contributorViewController)
        case .setting:
            break
        case .survey:
            let urlString = "https://docs.google.com/forms/d/e/1FAIpQLSfQHIwT0lf-20tx5xgUFSm7PPy_EjD5lI8SHuxV3DHN4D9pkA/viewform"
            if let url = URL(string: urlString) {
                presentSafariViewController(with: url)
            }
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

    private func presentSafariViewController(with url: URL) {
        let safariViewController = SFSafariViewController(url: url)
        present(safariViewController, animated: true)
    }
}
