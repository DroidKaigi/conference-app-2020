import ioscombined
import RxSwift

protocol AnnouncementsDataProviderProtocol {
    func fetch() -> Single<[Announcement]>
}

final class AnnouncementsDataProvider: AnnouncementsDataProviderProtocol {
    enum Transformer {
        static let dateFormatter: DateFormatter = {
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX"
            return formatter
        }()

        static func transform(_ response: String) -> Announcement.Type_? {
            switch response {
            case "NOTIFICATION":
                return .notification
            case "ALERT":
                return .alert
            case "FEEDBACK":
                return .feedback
            default:
                return .none
            }
        }

        static func transform(_ response: AnnouncementResponse) -> Announcement? {
            guard let publishedAt = dateFormatter.date(from: response.publishedAt)?.timeIntervalSince1970 else {
                return .none
            }
            guard let type = transform(response.type) else {
                return .none
            }

            return Announcement(
                id: response.id_,
                title: response.title,
                content: response.content,
                publishedAt: publishedAt,
                type: type
            )
        }
    }

    func fetch() -> Single<[Announcement]> {
        Single.create { observer in
            ApiComponentKt.generateDroidKaigiApi().getAnnouncements(
                lang: LangParameter.from(LangKt.defaultLang()),
                callback: { response in
                    let response = response.compactMap(Transformer.transform).sorted { $0.publishedAt >= $1.publishedAt }
                    observer(.success(response))
                },
                onError: { exception in
                    observer(.error(KotlinError(localizedDescription: exception.description())))
                }
            )

            return Disposables.create()
        }
    }
}
