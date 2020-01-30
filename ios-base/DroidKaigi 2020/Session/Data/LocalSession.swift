import RealmSwift
import ios_combined

final class LocalSession: Object {
	
	@objc dynamic var dayNumber: Int = 0
	@objc dynamic var desc: String = ""
	@objc dynamic var endTime: Double = 0
	@objc dynamic var hasIntendedAudience: Bool = false
	@objc dynamic var hasSpeaker: Bool = false
	@objc dynamic var id: String = ""
	@objc dynamic var isFavorited: Bool = false
	@objc dynamic var isOnGoing: Bool = false
	@objc dynamic var isFinished: Bool = false
	
	init(session: Session) {
		self.dayNumber = Int(session.dayNumber)
		self.desc = session.desc
		self.endTime = session.endTime
		self.hasIntendedAudience = session.hasIntendedAudience
		self.hasSpeaker = session.hasSpeaker
		self.id = session.id.id
		self.isFavorited = session.isFavorited
		self.isOnGoing = session.isOnGoing
		self.isFinished = session.isFinished		
	}
	
	required init() {
		fatalError("init() has not been implemented")
	}
	
}
