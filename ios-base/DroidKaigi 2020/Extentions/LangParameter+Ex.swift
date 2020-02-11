import Foundation
import ioscombined

extension LangParameter {
    static func from(_ lang: Lang) -> LangParameter {
        switch lang {
        case .en:
            return .en
        case .ja:
            return .jp
        default:
            return .en
        }
    }
}
