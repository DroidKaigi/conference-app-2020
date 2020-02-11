@testable import DroidKaigi_2020
import ioscombined
import RxCocoa
import RxSwift
import SnapshotTesting
import XCTest

class AnnouncementsViewControllerTests: XCTestCase {
    class AnnouncementsDataProviderMock: AnnouncementsDataProviderProtocol {
        func fetch() -> Single<[Announcement]> {
            Single.just([
                Announcement(id: 0, title: "フィードバックのお願い", content: "セッションやDroidKaigi 2020へのフィードバックを受け付けております。", publishedAt: 1_582_167_600, type: .alert),
                Announcement(id: 0, title: "フィードバックのお願い", content: "セッションやDroidKaigi 2020へのフィードバックを受け付けております。未回答の方はご協力ください。DroidKaigi 2020にご来場いただき、ありがとうございました。", publishedAt: 1_582_167_600, type: .feedback),
                Announcement(id: 0, title: "フィードバックのお願い", content: "セッションやDroidKaigi 2020へのフィードバックを受け付けております。未回答の方はご協力ください。DroidKaigi 2020にご来場いただき、ありがとうございました。", publishedAt: 1_582_167_600, type: .notification),
            ])
        }
    }

    private var controller: AnnouncementsViewController!

    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.
        controller = AnnouncementsViewController(viewModel: AnnouncementsViewModel(provider: AnnouncementsDataProviderMock()))
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func test_表示確認() {
        assertSnapshot(matching: controller, as: .image(on: .iPhoneSe))
        assertSnapshot(matching: controller, as: .image(on: .iPhoneX))
    }
}
