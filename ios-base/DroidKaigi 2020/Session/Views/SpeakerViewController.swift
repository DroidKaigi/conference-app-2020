import Foundation
import ios_combined
import UIKit

final class SpeakerViewController: UIViewController {
    @IBOutlet weak var userImageView: UIImageView! {
        didSet {
            userImageView.clipsToBounds = true
            userImageView.layer.cornerRadius = userImageView.bounds.width / 2
        }
    }
    @IBOutlet weak var userNameLabel: UILabel!

    private var speaker: Speaker!

    static func instantiate(speaker: Speaker) -> SpeakerViewController {
        let viewController =  UIStoryboard(name: "SpeakerViewController", bundle: .main).instantiateInitialViewController() as! SpeakerViewController
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
        if let imageUrl = speaker.imageUrl {
            userImageView.loadImage(url: URL(string: imageUrl))
        }
        userNameLabel.text = speaker.name
    }
}
