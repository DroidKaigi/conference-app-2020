import ios_combined
import RealmSwift
import RxRealm
import RxSwift

final class BookingSessionProvider {
    func bookSession(session: Session) -> Single<Void> {
        do {
            let realm = try Realm()
            let localSession = LocalSession(session: session)
            try realm.write {
                realm.add(localSession)
            }
            return .just(())
        } catch {
            return .error(error)
        }
    }

    func fetchBookedSessions() -> Observable<[LocalSession]> {
        do {
            let realm = try Realm()
            let result = realm.objects(LocalSession.self)
            let a = Observable.collection(from: result)
            return a.map { Array($0) }
        } catch {
            return .error(error)
        }
    }

    func resignBookingSession(_ session: LocalSession) -> Single<Void> {
        do {
            let realm = try Realm()
            try realm.write {
                realm.delete(session)
            }
            return .just(())
        } catch {
            return .error(error)
        }
    }
}
