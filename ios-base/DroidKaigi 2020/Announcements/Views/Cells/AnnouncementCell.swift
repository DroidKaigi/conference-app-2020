import ios_combined
import UIKit

private extension Announcement.Type_ {
    var iconImage: UIImage? {
        switch self {
        case .alert:
            return #imageLiteral(resourceName: "warning_amber_24px")
        case .feedback:
            return #imageLiteral(resourceName: "assignment_24px")
        case .notification:
            return #imageLiteral(resourceName: "error_outline_24px")
        default:
            return nil
        }
    }
}

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
        iconImageView.image = announcement.type.iconImage
        publishedAtLabel.text = Constant.dateFormatter.string(from: Date(timeIntervalSince1970: announcement.publishedAt))
        titleLabel.text = announcement.title
        contentLabel.text = announcement.content
    }
}
