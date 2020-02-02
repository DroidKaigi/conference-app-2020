import ios_combined
import RealmSwift

final class AppSpeaker: Object {
    @objc dynamic var id: AppSpeakerId = .init()
    @objc dynamic var name: String = ""
    @objc dynamic var tagLine: String?
    @objc dynamic var bio: String?
    @objc dynamic var imageUrl: String?
}
