import ios_combined
import RxCocoa
import RxSwift
import UIKit

final class SessionDetailViewController: UIViewController {
    var session: Session!

    // MARK: Private

    private let disposeBag = DisposeBag()

    // MARK: Outlets

    @IBOutlet private weak var titleLabel: UILabel!
    @IBOutlet private weak var dateLabel: UILabel!
    @IBOutlet private weak var timeRoomLabel: UILabel!
    @IBOutlet private weak var categoryContainer: UIStackView!
    @IBOutlet private weak var descriptionLabel: UILabel!
    @IBOutlet private weak var readMoreButton: UIButton!

    // MARK: Life cycles

    override func viewDidLoad() {
        super.viewDidLoad()

        bind()
        loadSession(session)
    }
}

private extension SessionDetailViewController {
    func loadSession(_ session: Session) {
        titleLabel.text = session.title.ja
        dateLabel.text = session.startDayText
        timeRoomLabel.text = session.timeRoomText
        descriptionLabel.text = session.desc
        readMoreButton.isHidden = !descriptionLabel.isTruncated
        print(session)
    }

    func bind() {
        disposeBag.insert(
            readMoreButton.rx.tap
                .bind(onNext: { [readMoreButton, descriptionLabel] in
                    descriptionLabel?.numberOfLines = 0
                    readMoreButton?.isHidden = true
                })
        )
    }
}
