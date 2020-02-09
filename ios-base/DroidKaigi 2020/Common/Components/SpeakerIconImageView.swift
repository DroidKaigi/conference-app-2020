import UIKit

class SpeakerIconImageView: UIImageView {
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }

    override init(image: UIImage?) {
        super.init(image: image)
        commonInit()
    }

    override init(image: UIImage?, highlightedImage: UIImage?) {
        super.init(image: image, highlightedImage: highlightedImage)
        commonInit()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        layer.cornerRadius = frame.width / 2
    }

    override func updateConstraints() {
        heightAnchor.constraint(equalTo: widthAnchor, multiplier: 1.0).isActive = true
        super.updateConstraints()
    }

    override var intrinsicContentSize: CGSize {
        .init(width: frame.height, height: frame.height)
    }
}

private extension SpeakerIconImageView {
    func commonInit() {
        layer.backgroundColor = UIColor.clear.cgColor
        layer.masksToBounds = true
    }
}
