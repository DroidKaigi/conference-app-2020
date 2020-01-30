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
	
}
