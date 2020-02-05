import ios_combined
import RealmSwift
import RxRealm
import RxSwift

final class BookingSessionProvider {
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
    func fetchBookedSessions(firstSessionStartTime: TimeInterval = 1582160400) -> Observable<[Session]> {
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
