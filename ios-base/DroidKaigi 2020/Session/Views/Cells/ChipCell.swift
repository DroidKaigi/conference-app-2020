import Material
import UIKit

final class ChipCell: CollectionViewCell {
    @IBOutlet weak var chipTitleLabel: UILabel!

    @IBOutlet weak var removeIcon: UIButton! {
        didSet {
            let image = removeIcon.imageView?.image?.withRenderingMode(.alwaysTemplate)
            removeIcon.imageView?.image = image?.tint(with: Asset.onSurfaceMediumEmphasis.color)
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        layer.cornerRadius = frame.height / 2
        clipsToBounds = true
    }
}
