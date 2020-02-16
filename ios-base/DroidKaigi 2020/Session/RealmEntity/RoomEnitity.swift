import ioscombined
import RealmSwift

final class RoomEntity: Object {
    @objc dynamic var id: Int = 0
    @objc dynamic var name: String = ""
    @objc dynamic var enName: String = ""
    @objc dynamic var sort: Int = 0

    init(room: Room) {
        id = Int(room.id)
        name = room.name.ja
        enName = room.name.en
        sort = Int(room.sort)
    }

    required init() {
        super.init()
    }
}
