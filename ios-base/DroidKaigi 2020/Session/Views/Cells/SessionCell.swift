import MaterialComponents
import Nuke
import UIKit

final class SessionCell: UICollectionViewCell {
    static let identifier = "SessionCell"
    static let rowHeight: CGFloat = 120

    @IBOutlet weak var liveBadge: UIView! {
        didSet {
            liveBadge.layer.cornerRadius = 5
            liveBadge.clipsToBounds = true
        }
    }

    @IBOutlet weak var bookmarkButton: UIButton! {
        didSet {
            let bookmarkImage = UIImage(named: "ic_bookmark")
            let templatedBookmarkImage = bookmarkImage?.withRenderingMode(.alwaysTemplate)
            bookmarkButton.setImage(templatedBookmarkImage, for: .selected)
            let bookmarkBorderImage = UIImage(named: "ic_bookmark_border")
            let templatedBookmarkBorderImage = bookmarkBorderImage?.withRenderingMode(.alwaysTemplate)
            bookmarkButton.setImage(templatedBookmarkBorderImage, for: .normal)
            bookmarkButton.tintColor = UIColor(hex: "00B5E2")
        }
    }

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var minutesAndRoomLabel: UILabel!
    @IBOutlet weak var speakersStackView: UIStackView!

    override func awakeFromNib() {
        super.awakeFromNib()
        translatesAutoresizingMaskIntoConstraints = false
        widthAnchor.constraint(equalToConstant: UIScreen.main.bounds.width).isActive = true
    }

    func addSpeakerView(imageURL: URL?, speakerName: String) {
        let view = UIView()
        let speakerIconView = UIImageView()
        if let imageURL = imageURL {
            let options = ImageLoadingOptions(transition: .fadeIn(duration: 0.3))
            Nuke.loadImage(with: imageURL, options: options, into: speakerIconView)
        }
        view.addSubview(speakerIconView)
        speakerIconView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            speakerIconView.topAnchor.constraint(equalTo: view.topAnchor),
            speakerIconView.leftAnchor.constraint(equalTo: view.leftAnchor),
            speakerIconView.heightAnchor.constraint(equalToConstant: 32),
            speakerIconView.widthAnchor.constraint(equalToConstant: 32),
        ])

        let speakerNameLabel = UILabel()
        speakerNameLabel.text = speakerName
        view.addSubview(speakerNameLabel)
        speakerNameLabel.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            speakerNameLabel.leftAnchor.constraint(equalTo: speakerIconView.rightAnchor, constant: 8),
            speakerNameLabel.centerYAnchor.constraint(equalTo: speakerIconView.centerYAnchor),
        ])
        speakerNameLabel.font = UIFont.systemFont(ofSize: 12, weight: .light)

        speakersStackView.addArrangedSubview(view)
        view.translatesAutoresizingMaskIntoConstraints = false
        view.heightAnchor.constraint(equalToConstant: 33).isActive = true

        speakerIconView.layer.cornerRadius = 16
        speakerIconView.clipsToBounds = true
    }

    override func prepareForReuse() {
        super.prepareForReuse()

        speakersStackView.subviews.forEach { subview in
            subview.removeFromSuperview()
        }
        speakersStackView.arrangedSubviews.forEach { subview in
            speakersStackView.removeArrangedSubview(subview)
        }
    }
}
