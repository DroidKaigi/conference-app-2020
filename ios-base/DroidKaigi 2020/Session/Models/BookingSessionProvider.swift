import ios_combined
import RealmSwift
import RxSwift

final class BookingSessionProvider {
    func bookSession(session: AppBaseSession) -> Single<Void> {
        return Single.create { (observer) -> Disposable in
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
                observer(.success(()))
            } catch {
                observer(.error(error))
            }
            return Disposables.create()
        }
    }

    func fetchBookedSessions() -> [AppBaseSession] {
        do {
            let realm = try Realm()
            let serviceResult = Array(realm.objects(AppServiceSession.self)).map { $0 as AppBaseSession }
            let speechResult = Array(realm.objects(AppSpeechSession.self)).map { $0 as AppBaseSession }
            return serviceResult + speechResult
        } catch {
            return []
        }
    }

    func resignBookingSession(_ session: AppBaseSession) -> Single<Void> {
        return Single.create { (observer) -> Disposable in
            do {
                let realm = try Realm()
                if let serviceSession = session as? AppServiceSession, !serviceSession.isInvalidated {
                    try realm.write {
                        realm.delete(serviceSession)
                        try realm.commitWrite()
                    }
                } else if let speechSession = session as? AppSpeechSession, !speechSession.isInvalidated {
                    try realm.write {
                        realm.delete(speechSession)
                        try realm.commitWrite()
                    }
                }
                observer(.success(()))
            } catch {
                observer(.error(error))
            }
            return Disposables.create()
        }
    }
}
