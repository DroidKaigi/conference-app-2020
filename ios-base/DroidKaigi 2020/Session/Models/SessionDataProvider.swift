import ios_combined
import RxSwift

final class SessionDataProvider {
    func fetchSessions() -> Single<[Session]> {
        return Single.create { observer -> Disposable in
            ApiComponentKt.generateDroidKaigiApi().getSessions(callback: { response in
                let model = ResponseToModelMapperKt.toModel(response)
                observer(.success(model.sessions))
            }) { error in
                observer(.error(KotlinError(localizedDescription: error.description())))
            }
            return Disposables.create()
        }
    }
}
