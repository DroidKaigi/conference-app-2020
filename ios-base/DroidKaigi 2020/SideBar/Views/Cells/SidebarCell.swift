import UIKit

final class SidebarCell: UITableViewCell {
    let notSelectedColor = UIColor(hex: "A8A8A8")

    @IBOutlet weak var iconImageView: UIImageView! {
        didSet {
            iconImageView.image = iconImageView.image?
                .withRenderingMode(.alwaysTemplate)
                .tint(with: notSelectedColor)
        }
    }

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.textColor = notSelectedColor
        }
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        selectionStyle = .none
        if selected {
            iconImageView.image = iconImageView.image?.tint(with: ApplicationScheme.shared.colorScheme.primaryColor)
            titleLabel.textColor = ApplicationScheme.shared.colorScheme.primaryColor
        } else {
            iconImageView.image = iconImageView.image?.tint(with: notSelectedColor)
            titleLabel.textColor = notSelectedColor
        }
    }
}
