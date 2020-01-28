import Foundation
import RxCocoa
import RxSwift

extension Observable where Element: OptionalType {
    func filterNil() -> Observable<Element.Wrapped> {
        flatMap { element -> Observable<Element.Wrapped> in
            guard let value = element.value else { return .empty() }
            return .just(value)
        }
    }
}

extension SharedSequenceConvertibleType where Element: OptionalType {
    func filterNil() -> SharedSequence<Self.SharingStrategy, Element.Wrapped> {
        flatMap { element -> SharedSequence<Self.SharingStrategy, Element.Wrapped> in
            guard let value = element.value else { return .empty() }
            return .just(value)
        }
    }
}

// MARK: -

protocol OptionalType {
    associatedtype Wrapped

    var value: Wrapped? { get }
}

extension Optional: OptionalType {
    var value: Wrapped? { self }
}
