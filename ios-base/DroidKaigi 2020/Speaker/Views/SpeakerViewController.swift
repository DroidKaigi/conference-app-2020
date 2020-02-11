import Foundation
import ioscombined
import Nuke
import UIKit

final class SpeakerViewController: UIViewController {
    @IBOutlet weak var stackView: UIStackView!
    @IBOutlet weak var userImageView: UIImageView! {
        didSet {
            userImageView.clipsToBounds = true
            userImageView.layer.cornerRadius = userImageView.bounds.width / 2
        }
    }

    @IBOutlet weak var userNameLabel: UILabel!
    @IBOutlet weak var tagLabel: UILabel!
    @IBOutlet weak var biographyLabel: UILabel!
    @IBOutlet weak var sessionSectionLabel: UILabel! {
        didSet {
            sessionSectionLabel.text = L10n.sessions
        }
    }

    private var speaker: Speaker!
    private var sessions: [Session]!

    static func instantiate(speaker: Speaker, sessions: [Session]) -> SpeakerViewController {
        guard let viewController = UIStoryboard(name: "SpeakerViewController", bundle: .main).instantiateInitialViewController() as? SpeakerViewController else { fatalError() }
        viewController.speaker = speaker
        viewController.sessions = sessions
        return viewController
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.navigationBar.barTintColor = .white
        navigationController?.navigationBar.tintColor = .black
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

        sessions.forEach { session in
            let sessionView = SpeakerSessionView.instantiate()
            sessionView.sessionTitleLabel.text = session.title.currentLangString
            sessionView.sessionDateLabel.text = session.currentLangShortSummary
            stackView.insertArrangedSubview(sessionView, at: stackView.arrangedSubviews.count)
        }
    }
}
