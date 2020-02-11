import ioscombined
import Nuke
import UIKit

final class ResultSpeakerCell: UICollectionViewCell {
    static let rowHeight: CGFloat = 60
    static let identifier = "ResultSpeakerCell"

    @IBOutlet weak var speakerIcon: UIImageView! {
        didSet {
            speakerIcon.layer.cornerRadius = speakerIcon.frame.height / 2
            speakerIcon.clipsToBounds = true
        }
    }

    @IBOutlet weak var speakerNameLabel: UILabel!

    func configure(speaker: Speaker) {
        if let imageURL = URL(string: speaker.imageUrl ?? "") {
            let options = ImageLoadingOptions(transition: .fadeIn(duration: 0.3))
            Nuke.loadImage(with: imageURL, options: options, into: speakerIcon)
        }
        speakerNameLabel.text = speaker.name
    }
}
