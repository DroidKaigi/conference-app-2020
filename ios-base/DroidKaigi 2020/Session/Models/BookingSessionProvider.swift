import ios_combined
import RealmSwift
import RxSwift

final class BookingSessionProvider {
    func bookSession(session: Session) -> Completable {
        do {
            let realm = try Realm()
            let localSession = LocalSession(session: session)
            return realm.rx.write(object: localSession)
        } catch {
            return .error(error)
        }
    }
}
