import Material
import UIKit

final class SearchContentsViewController: UIViewController {

    var tableView: TableView!

    override func viewDidLoad() {
        super.viewDidLoad()
    }

    private func setUpTableView() {
        tableView = TableView(frame: .zero, style: .plain)
        view.addSubview(tableView)
    }
}
