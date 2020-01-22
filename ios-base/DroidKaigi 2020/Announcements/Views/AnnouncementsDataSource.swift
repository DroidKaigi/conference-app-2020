import UIKit
import RxSwift
import RxCocoa
import ios_combined

final class AnnouncementsDataSource: NSObject, RxTableViewDataSourceType, UITableViewDataSource {
    typealias Element = [Announcement]

    var items = [Announcement]()

    func tableView(_ tableView: UITableView, observedEvent: Event<[Announcement]>) {
        Binder(self) { target, items in
            target.items = items
            tableView.reloadData()
        }
        .on(observedEvent)
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return UITableViewCell()
    }
}
