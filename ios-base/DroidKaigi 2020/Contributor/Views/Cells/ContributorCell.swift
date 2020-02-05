import MaterialComponents
import UIKit

final class ContributorCell: MDCCardCollectionCell {
    static let identifier: String = "ContributorCell"
    static let rowHeight: CGFloat = 76

    @IBOutlet weak var iconImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!

    override func prepareForReuse() {
        super.prepareForReuse()

        iconImageView.image = nil
    }
}
