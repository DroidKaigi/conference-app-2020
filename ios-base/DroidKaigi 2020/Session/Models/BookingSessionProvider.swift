import ioscombined
import RealmSwift
import RxRealm
import RxSwift

final class BookingSessionProvider {
    init() {
        let config = Realm.Configuration(
            schemaVersion: 1,
            migrationBlock: { _, oldSchemaVersion in
                if oldSchemaVersion < 1 {}
            }
        )
        Realm.Configuration.defaultConfiguration = config
    }

    func bookSession(_ session: Session) {
        do {
            let realm = try Realm()
            let session = SessionEntity(session: session)
            session.isFavorited = true
            try realm.write {
                realm.add(session)
            }
        } catch {
            fatalError(error.localizedDescription)
        }
    }

    // 1582160400 is 2/20 10:00
    func fetchBookedSessions(firstSessionStartTime: TimeInterval = 1_582_160_400) -> Observable<[Session]> {
        do {
            let realm = try Realm()
            let result = realm.objects(SessionEntity.self)

            return Observable.collection(from: result).map { (results) -> [Session] in
                results.compactMap { (sessionEntity) -> Session? in
                    return RealmToModelMapper.toModel(sessionEntity: sessionEntity, firstSessionSTime: firstSessionStartTime)
                }
            }
        } catch {
            return .error(error)
        }
    }

    func resignBookingSession(_ sessionId: String) {
        do {
            let realm = try Realm()
            guard let session = realm.object(ofType: SessionEntity.self, forPrimaryKey: sessionId) else {
                fatalError()
            }
            try realm.write {
                realm.delete(session)
            }
        } catch {
            fatalError(error.localizedDescription)
        }
    }
}
