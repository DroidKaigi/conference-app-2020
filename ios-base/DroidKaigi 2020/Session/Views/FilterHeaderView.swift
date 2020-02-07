import Material
import UIKit

final class FilterHeaderView: UIView {
    @IBOutlet weak var filterLabel: UILabel! {
        didSet {
            filterLabel.text = L10n.filter
        }
    }

    @IBOutlet weak var resetButton: Button! {
        didSet {
            resetButton.layer.cornerRadius = 4
            resetButton.clipsToBounds = true
            resetButton.setTitle(L10n.reset, for: .normal)
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        loadNib()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        loadNib()
    }

    func loadNib() {
        if let view = Bundle.main.loadNibNamed("FilterHeaderView", owner: self, options: nil)?.first as? UIView {
            view.frame = bounds
            addSubview(view)
        }
    }
}
