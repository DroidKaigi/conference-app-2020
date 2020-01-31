import UIKit

final class AboutCell: UITableViewCell {
    struct Identifier {
        static let description = "description"
        static let icon = "icon"
        static let basic = "basic"
        static let detail = "detail"
    }

    enum Button: Int {
        case twitter, youtube, medium
    }

    var onButtonTapped: ((Button) -> Void)?

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var detailLabel: UILabel?

    @IBAction func onButtonTapped(_ button: UIButton) {
        guard let button = Button(rawValue: button.tag) else { return }
        onButtonTapped?(button)
    }
}
