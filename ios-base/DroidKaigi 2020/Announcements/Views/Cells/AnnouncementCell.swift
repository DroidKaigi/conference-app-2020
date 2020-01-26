import ios_combined
import UIKit

final class AnnouncementCell: UITableViewCell {
    enum Constant {
        static let dateFormatter: DateFormatter = {
            let formatter = DateFormatter()
            formatter.locale = Locale.current
            formatter.calendar = Calendar.current
            formatter.dateFormat = DateFormatter.dateFormat(fromTemplate: "dMMM HH:mm", options: 0, locale: Locale.current)
            return formatter
        }()
    }

    @IBOutlet weak var iconImageView: UIImageView!
    @IBOutlet weak var publishedAtLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!

    func configura(_ announcement: Announcement) {
        // TODO: Set icon image
        publishedAtLabel.text = Constant.dateFormatter.string(from: Date(timeIntervalSince1970: announcement.publishedAt))
        titleLabel.text = announcement.title
        contentLabel.text = announcement.content
    }
}
