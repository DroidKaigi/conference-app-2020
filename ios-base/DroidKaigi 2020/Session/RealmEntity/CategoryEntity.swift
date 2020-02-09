import ios_combined
import RealmSwift

final class CategoryEntity: Object {
    @objc dynamic var id: Int = 0
    @objc dynamic var name: String = ""
    @objc dynamic var enName: String = ""
    
    init(category: ios_combined.Category) {
        id = Int(category.id)
        name = category.name.ja
        enName = category.name.en
    }
    
    required init() {
        super.init()
    }
}

