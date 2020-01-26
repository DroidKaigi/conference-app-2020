import Foundation
import RxSwift

extension Observable where Element: OptionalType {
    func filterNil() -> Observable<Element.Wrapped> {
        flatMap { element -> Observable<Element.Wrapped> in
            guard let value = element.value else {
                return .empty()
            }
            return .just(value)
        }
    }
}
