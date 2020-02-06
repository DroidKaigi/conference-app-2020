import UIKit
import RxSwift
import RxCocoa

extension UILabel {
    var isTruncated: Bool {
        guard let text = text else { return false }

        let textSize = (text as NSString).boundingRect(
            with: CGSize(width: frame.size.width, height: .greatestFiniteMagnitude),
            options: .usesLineFragmentOrigin,
            attributes: [.font: font ?? UIFont.systemFont(ofSize: UIFont.systemFontSize)],
            context: nil).size

        return textSize.height > bounds.size.height
    }
}
