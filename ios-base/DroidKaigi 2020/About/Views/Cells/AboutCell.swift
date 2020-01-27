import UIKit

class AboutCell: UITableViewCell {
    struct Identifier {
        static let description = "description"
        static let icon = "icon"
        static let basic = "basic"
        static let detail = "detail"
    }

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var detailLabel: UILabel?
}
