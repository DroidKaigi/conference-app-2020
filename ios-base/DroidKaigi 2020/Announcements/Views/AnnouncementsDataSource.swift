import ioscombined
import RxCocoa
import RxSwift
import UIKit

final class AnnouncementsDataSource: NSObject, RxTableViewDataSourceType, UITableViewDataSource {
    typealias Element = [Announcement]

    private var items = [Announcement]()

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
        let cell = tableView.dequeueReusableCell(withIdentifier: String(describing: AnnouncementCell.self), for: indexPath) as! AnnouncementCell // swiftlint:disable:this force_cast
        cell.configure(items[indexPath.item])
        return cell
    }
}
