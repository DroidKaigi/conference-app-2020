import Foundation
import ios_combined
import Nuke
import UIKit

final class SpeakerViewController: UIViewController {
    @IBOutlet weak var userImageView: UIImageView! {
        didSet {
            userImageView.clipsToBounds = true
            userImageView.layer.cornerRadius = userImageView.bounds.width / 2
        }
    }

    @IBOutlet weak var userNameLabel: UILabel!
    @IBOutlet weak var tagLabel: UILabel!
    @IBOutlet weak var biographyLabel: UILabel!

    private var speaker: Speaker!

    static func instantiate(speaker: Speaker) -> SpeakerViewController {
        guard let viewController = UIStoryboard(name: "SpeakerViewController", bundle: .main).instantiateInitialViewController() as? SpeakerViewController else { fatalError() }
        viewController.speaker = speaker
        return viewController
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        setupUI()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.navigationBar.tintColor = .black
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }

    private func setupUI() {
        if
            let imageUrl = speaker.imageUrl,
            let url = URL(string: imageUrl) {
            Nuke.loadImage(with: url, into: userImageView)
        }
        userNameLabel.text = speaker.name
        tagLabel.text = speaker.tagLine
        biographyLabel.text = speaker.bio
    }
}
