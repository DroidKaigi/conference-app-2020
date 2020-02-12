import ioscombined
import Nuke
import RxCocoa
import RxSwift
import UIKit

final class SessionDetailViewController: UIViewController {
    var session: Session!

    // MARK: Private

    private let disposeBag = DisposeBag()

    // MARK: Outlets

    @IBOutlet private weak var headerSection: UIView!
    @IBOutlet private weak var titleLabel: UILabel!
    @IBOutlet private weak var dateLabel: UILabel!
    @IBOutlet private weak var timeRoomLabel: UILabel!
    @IBOutlet private weak var categoryContainer: UIStackView!
    @IBOutlet private weak var categoryLabel: CategoryLabel!
    @IBOutlet private weak var langLabel: CategoryLabel!

    @IBOutlet private weak var descriptionSection: UIView!
    @IBOutlet private weak var descriptionLabel: UILabel!
    @IBOutlet private weak var readMoreButton: UIButton!

    @IBOutlet private weak var audienceSection: UIView!
    @IBOutlet private weak var intendedAudienceLabel: UILabel!

    @IBOutlet private weak var speakersSection: UIView!
    @IBOutlet private weak var speakersContainer: UIStackView!

    @IBOutlet private weak var docsSection: UIView!
    @IBOutlet private weak var videoButton: UIButton!
    @IBOutlet private weak var slideButton: UIButton!

    @IBOutlet private weak var shareButton: UIBarButtonItem!
    @IBOutlet private weak var placeButton: UIBarButtonItem!
    @IBOutlet private weak var eventButton: UIBarButtonItem!

    // MARK: Life cycles

    override func viewDidLoad() {
        super.viewDidLoad()

        bind()
        loadSession(session)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.navigationBar.barTintColor = .white
        navigationController?.navigationBar.tintColor = .black
    }
}

// MARK: - Private functions

// MARK: Create view

private extension SessionDetailViewController {
    func loadSession(_ session: Session) {
        titleLabel.text = session.title.currentLangString
        dateLabel.text = "\(session.startDayText) \(session.startTimeText)-"
        timeRoomLabel.text = session.timeRoomText
        descriptionLabel.text = session.desc
        readMoreButton.isHidden = !descriptionLabel.isTruncated

        switch session {
        case let speech as SpeechSession:
            loadSpeechSession(speech)
        case let service as ServiceSession:
            loadServiceSession(service)
        default: break
        }
    }

    func loadSpeechSession(_ session: SpeechSession) {
        // FIXME: How to detect current lang?
        categoryLabel.text = session.category.name.currentLangString
        langLabel.text = session.lang.text.currentLangString

        if let intendedAudience = session.intendedAudience {
            intendedAudienceLabel.text = intendedAudience
        } else {
            audienceSection.isHidden = true
        }
        if session.speakers.isEmpty {
            speakersSection.isHidden = true
        } else {
            session.speakers.forEach(addSpeaker(_:))
        }
        if let videoUrl = session.videoUrl.flatMap(URL.init) {
            videoButton.isEnabled = true
            disposeBag.insert(
                videoButton.rx.tap
                    .bind(onNext: { [unowned self] in self.openURL(videoUrl) })
            )
        } else {
            videoButton.isEnabled = false
        }
        if let slideUrl = session.slideUrl.flatMap(URL.init) {
            slideButton.isEnabled = true
            disposeBag.insert(
                videoButton.rx.tap
                    .bind(onNext: { [unowned self] in self.openURL(slideUrl) })
            )
        } else {
            slideButton.isEnabled = false
        }
    }

    func loadServiceSession(_ session: ServiceSession) {
        [
            categoryLabel,
            langLabel,
            descriptionSection,
            audienceSection,
            speakersSection,
            docsSection,
        ]
        .compactMap { $0 }
        .forEach { $0.isHidden = true }
    }

    func addSpeaker(_ speaker: Speaker) {
        let iconImageView = SpeakerIconImageView()
        if let imageUrl = speaker.imageUrl.flatMap(URL.init) {
            let options = ImageLoadingOptions(transition: .fadeIn(duration: 0.3))
            Nuke.loadImage(with: imageUrl, options: options, into: iconImageView)
        }

        let nameLabel = UILabel()
        nameLabel.font = .systemFont(ofSize: 14, weight: .light)
        nameLabel.textColor = UIColor(hex: "00B5E2")
        nameLabel.text = speaker.name
        let speakerViewContainer = UIView()
        let speakerView = UIStackView(arrangedSubviews: [iconImageView, nameLabel])
        speakerView.spacing = 16.0
        let speakerButton: UIButton = {
            let button = UIButton()
            button.setTitle(nil, for: .normal)
            return button
        }()
        [iconImageView, nameLabel, speakerView, speakerViewContainer, speakerButton].forEach {
            $0.translatesAutoresizingMaskIntoConstraints = false
        }
        [speakerView, speakerButton].forEach(speakerViewContainer.addSubview(_:))
        speakersContainer.addArrangedSubview(speakerViewContainer)

        NSLayoutConstraint.activate([
            iconImageView.heightAnchor.constraint(equalToConstant: 60),
            speakerView.topAnchor.constraint(equalTo: speakerViewContainer.topAnchor),
            speakerView.bottomAnchor.constraint(equalTo: speakerViewContainer.bottomAnchor),
            speakerView.leadingAnchor.constraint(equalTo: speakerViewContainer.leadingAnchor),
            speakerView.trailingAnchor.constraint(equalTo: speakerViewContainer.trailingAnchor),
            speakerView.topAnchor.constraint(equalTo: speakerButton.topAnchor),
            speakerView.bottomAnchor.constraint(equalTo: speakerButton.bottomAnchor),
            speakerView.leadingAnchor.constraint(equalTo: speakerButton.leadingAnchor),
            speakerView.trailingAnchor.constraint(equalTo: speakerButton.trailingAnchor),
        ])
        disposeBag.insert(
            speakerButton.rx.tap
                .bind { [unowned self, speaker] in self.showSpeakerView(speaker) }
        )
    }
}

// MARK: Bind

private extension SessionDetailViewController {
    func bind() {
        disposeBag.insert(
            readMoreButton.rx.tap
                .bind { [readMoreButton, descriptionLabel] in
                    descriptionLabel?.numberOfLines = 0
                    readMoreButton?.isHidden = true
                },
            shareButton.rx.tap
                .bind { print("share") }, // TODO: Not implemented
            placeButton.rx.tap
                .bind { print("place") }, // TODO: Not implemented
            eventButton.rx.tap
                .bind { print("event") } // TODO: Not implemented
        )
    }
}

// MARK: Transition

private extension SessionDetailViewController {
    func showSpeakerView(_ speaker: Speaker) {
        // FIXME: Sessions should be all fetched
        let speakerView = SpeakerViewController.instantiate(speaker: speaker, sessions: [session])
        navigationController?.pushViewController(speakerView, animated: true)
    }

    func openURL(_ url: URL) {
        UIApplication.shared.open(url)
    }
}

// MARK: - To show Detail view

extension UIViewController {
    func showDetail(forSession session: Session) {
        // FIXME: Use coordinator?
        guard let vc = UIStoryboard(name: "SessionDetail", bundle: nil)
            .instantiateInitialViewController() as? SessionDetailViewController else {
            return
        }
        vc.session = session
        navigationController?.pushViewController(vc, animated: true)
    }
}
