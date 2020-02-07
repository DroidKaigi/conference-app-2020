import Material
import UIKit

final class FilterHeaderView: UIView {
    @IBOutlet weak var filterLabel: UILabel!
    @IBOutlet weak var resetButton: Button! {
        didSet {
            resetButton.layer.cornerRadius = 4
            resetButton.clipsToBounds = true
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
            view.frame = self.bounds
            self.addSubview(view)
        }
    }
}
