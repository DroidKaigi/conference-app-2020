import Material
import UIKit

enum SwitchViewControllerType: Int {
    case timeline
    case about
    case info
    case map
    case sponsor
    case contributor
    case setting
}

final class SidebarViewController: UITableViewController {
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
