import Foundation
import UIKit

final class SpeakerSessionView: UIView {
    static func instantiate() -> Self {
        let nib = UINib(nibName: String(describing: self), bundle: .main)
        guard let view = nib.instantiate(withOwner: nil, options: nil).first as? Self else { fatalError() }
        return view
    }

    @IBOutlet weak var sessionTitleLabel: UILabel!
    @IBOutlet weak var sessionDateLabel: UILabel!
}
