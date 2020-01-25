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

    weak var rootViewController: UIViewController?

    static func instantiate(rootViewController: UIViewController) -> SidebarViewController {
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
        guard
            let cell = tableView.cellForRow(at: indexPath),
            let switchType = SwitchViewControllerType(rawValue: cell.tag)
        else { return }

        switch switchType {
        case .timeline:
            rootViewController?.navigationDrawerController?.toggleLeftView()
        case .about:
            break
        case .info:
            break
        case .map:
            break
        case .sponsor:
            break
        case .contributor:
            break
        case .setting:
            break
        }
    }
}
