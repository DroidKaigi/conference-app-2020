import RealmSwift
import RxSwift

extension Realm: ReactiveCompatible {}

extension Reactive where Base == Realm {
    func write(object: Object) -> Completable {
        let config = base.configuration
        return Completable.create { observer -> Disposable in
            autoreleasepool {
                DispatchQueue.global().async {
                    do {
                        let realm: Realm = try Realm(configuration: config)
                        try realm.write {
                            realm.add(object)
                        }
                        observer(.completed)
                    } catch {
                        observer(.error(error))
                    }
                }
            }
            return Disposables.create()
        }
    }

    func fetch<T: Object>() -> Single<[T]> {
        let config = base.configuration
        return Single.create { observer -> Disposable in
            autoreleasepool {
                DispatchQueue.global().async {
                    do {
                        let realm: Realm = try Realm(configuration: config)
                        let objects = Array(realm.objects(T.self))
                        observer(.success(objects))
                    } catch {
                        observer(.error(error))
                    }
                }
            }
            return Disposables.create()
        }
    }
}
