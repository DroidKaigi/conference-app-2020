import ioscombined
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

    @IBOutlet weak var iconImageContainerView: UIView! {
        didSet {
            iconImageContainerView.clipsToBounds = true
            iconImageContainerView.backgroundColor = Asset.secondary50.color
            iconImageContainerView.layer.cornerRadius = 16
        }
    }

    @IBOutlet weak var iconImageView: UIImageView!
    @IBOutlet weak var publishedAtLabel: UILabel!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var contentTextView: UITextView!

    func configure(_ announcement: Announcement) {
        iconImageView.image = announcement.type.iconImage?.withRenderingMode(.alwaysOriginal).tint(with: Asset.secondary300.color)
        publishedAtLabel.text = Constant.dateFormatter.string(from: Date(timeIntervalSince1970: announcement.publishedAt))
        titleLabel.text = announcement.title
        contentTextView.attributedText = { content in
            guard
                let data = content.data(using: .unicode),
                let attributedString = try? NSMutableAttributedString(data: data, options: [.documentType: NSAttributedString.DocumentType.html], documentAttributes: nil)
            else {
                return nil
            }

            let range = NSRange(location: 0, length: attributedString.length)
            attributedString.addAttribute(.font, value: UIFont.preferredFont(forTextStyle: .body), range: range)
            return attributedString
        }(announcement.content)
    }
}
