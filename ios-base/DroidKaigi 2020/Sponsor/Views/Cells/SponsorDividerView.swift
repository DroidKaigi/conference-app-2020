import UIKit

final class SponsorDividerView: UICollectionReusableView {
    static let identifier = "SponsorDividerView"

    @IBOutlet private weak var dividerView: UIView!

    var isLastSection: Bool = false {
        didSet {
            if isLastSection {
                dividerView.backgroundColor = .clear
            } else {
                if #available(iOS 13.0, *) {
                    dividerView.backgroundColor = .systemFill
                } else {
                    dividerView.backgroundColor = .gray
                }
            }
        }
    }
}
