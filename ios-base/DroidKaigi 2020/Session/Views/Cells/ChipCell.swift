import Material
import UIKit

final class ChipCell: CollectionViewCell {
    static let identifier = "ChipCell"
    static let cellHeight: CGFloat = 32
    static let estimatedCellWidth: CGFloat = 100

    override var isSelected: Bool {
        didSet {
            if isSelected {
                backgroundColor = Asset.secondary300.color
                removeIcon.isHidden = false
                badgeImageView.isHidden = true
            } else {
                backgroundColor = Asset.primary400.color
                removeIcon.isHidden = true
                badgeImageView.isHidden = false
            }
        }
    }

    @IBOutlet weak var badgeImageView: UIImageView!
    @IBOutlet weak var chipTitleLabel: UILabel!
    @IBOutlet weak var removeIcon: UIImageView!

    override func awakeFromNib() {
        super.awakeFromNib()

        layer.cornerRadius = frame.height / 2
        clipsToBounds = true

        backgroundColor = Asset.primary400.color
    }
}
