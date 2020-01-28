import ios_combined
import UIKit

final class SessionDetailViewController: UIViewController {
    var session: Session!

    @IBOutlet private weak var titleLabel: UILabel!
    @IBOutlet private weak var dateLabel: UILabel!
    @IBOutlet private weak var timeRoomLabel: UILabel!

    // MARK: Life cycles

    override func viewDidLoad() {
        super.viewDidLoad()

        loadSession(session)
    }
}

private extension SessionDetailViewController {
    func loadSession(_ session: Session) {
        titleLabel.text = session.title.ja
        dateLabel.text = session.startDayText
        timeRoomLabel.text = session.timeRoomText
    }
}
