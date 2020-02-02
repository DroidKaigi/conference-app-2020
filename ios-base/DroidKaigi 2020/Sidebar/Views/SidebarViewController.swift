import Material
import UIKit

final class SidebarViewController: UITableViewController {
    private enum SwitchViewControllerType: Int {
        case timeline
        case about
        case info
        case map
        case sponsor
        case contributor
        case setting
    }

    weak var rootViewController: UINavigationController?

    static func instantiate(rootViewController: UINavigationController) -> SidebarViewController {
        guard let viewController = UIStoryboard(name: "SidebarViewController", bundle: .main).instantiateInitialViewController() as? SidebarViewController else { fatalError() }
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
        let indexPath = IndexPath(row: SwitchViewControllerType.timeline.rawValue, section: 0)
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
            break
        case .sponsor:
            let vc = SponsorViewController()
            rootViewController.pushViewController(vc, animated: true)
        case .contributor:
            break
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
