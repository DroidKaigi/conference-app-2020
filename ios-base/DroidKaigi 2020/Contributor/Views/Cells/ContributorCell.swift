import MaterialComponents
import UIKit

final class ContributorCell: MDCCardCollectionCell {
    static let identifier: String = "ContributorCell"
    static let rowHeight: CGFloat = 76

    @IBOutlet weak var iconImageView: UIImageView! {
        didSet {
            iconImageView.clipsToBounds = true
            iconImageView.layer.cornerRadius = iconImageView.frame.height / 2
        }
    }

    @IBOutlet weak var nameLabel: UILabel!

    override func prepareForReuse() {
        super.prepareForReuse()

        iconImageView.image = nil
    }
}
