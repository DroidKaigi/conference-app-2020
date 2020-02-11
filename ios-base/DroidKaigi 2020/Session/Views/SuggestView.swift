import UIKit

final class SuggestView: UIView {
    @IBOutlet weak var bookmarkImage: UIImageView! {
        didSet {
            bookmarkImage.image = bookmarkImage.image?.withRenderingMode(.alwaysTemplate)
        }
    }

    @IBOutlet weak var descriptionLabel: UILabel! {
        didSet {
            descriptionLabel.text = L10n.suggestDescription
        }
    }

    init() {
        super.init(frame: .zero)
        loadFromBundle()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        loadFromBundle()
    }

    private func loadFromBundle() {
        guard let view = Bundle.main.loadNibNamed("SuggestView", owner: self, options: nil)?.first as? UIView else {
            fatalError("can't load SuggestView.xib")
        }
        addSubview(view)

        view.translatesAutoresizingMaskIntoConstraints = false
        view.topAnchor.constraint(equalTo: topAnchor).isActive = true
        view.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        view.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
        view.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
    }
}
