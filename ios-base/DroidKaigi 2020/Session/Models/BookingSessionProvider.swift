import ios_combined
import RealmSwift
import RxRealm
import RxSwift

final class BookingSessionProvider {
    func bookSession(session: AppBaseSession) -> Single<Void> {
        do {
            let realm = try Realm()
            if let serviceSession = session as? AppServiceSession {
                serviceSession.isFavorited = true
                try realm.write {
                    realm.add(serviceSession)
                }
            } else if let speechSession = session as? AppSpeechSession {
                speechSession.isFavorited = true
                try realm.write {
                    realm.add(speechSession)
                }
            }
            return .just(())
        } catch {
            return .error(error)
        }
    }

    func fetchBookedSessions() -> Observable<[AppBaseSession]> {
        do {
            let realm = try Realm()
            let serviceResult = realm.objects(AppServiceSession.self)
            let speechResult = realm.objects(AppSpeechSession.self)
            return Observable.combineLatest(
                Observable.collection(from: serviceResult).map { Array($0) as [AppBaseSession] },
                Observable.collection(from: speechResult).map { Array($0) as [AppBaseSession] }
            ).map { $0 + $1 }
        } catch {
            return .error(error)
        }
    }

    func resignBookingSession(_ session: AppBaseSession) -> Single<Void> {
        do {
            let realm = try Realm()
            if let serviceSession = session as? AppServiceSession {
                try realm.write {
                    realm.delete(serviceSession)
                }
            } else if let speechSession = session as? AppSpeechSession {
                try realm.write {
                    realm.delete(speechSession)
                }
            }
            return .just(())
        } catch {
            return .error(error)
        }
    }
}
