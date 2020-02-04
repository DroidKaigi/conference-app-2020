import ios_combined
import RealmSwift
import RxRealm
import RxSwift

final class BookingSessionProvider {
    private let disposeBag = DisposeBag()

    func bookSession(session: Session) {
        do {
            let sessionId = session.id.id
            let realm = try Realm()

            if let session = realm.object(ofType: SessionEntity.self, forPrimaryKey: sessionId) {
                try realm.write {
                    session.isFavorited = true
                }
            } else {
                let session = SessionEntity(session: session)
                try realm.write {
                    realm.add(session)
                }
            }
        } catch {
            fatalError(error.localizedDescription)
        }
    }

    func fetchBookedSessions(firstSession: Session) -> Observable<[Session]> {
        do {
            let realm = try Realm()
            let result = realm.objects(SessionEntity.self)

            return Observable.collection(from: result).map { (results) -> [Session] in
                results.compactMap { (sessionEntity) -> Session? in
                    return RealmToModelMapper.toModel(sessionEntity: sessionEntity, firstSessionSTime: firstSession.startTime)
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
                session.isFavorited = false
            }
        } catch {
            fatalError(error.localizedDescription)
        }
    }
}
