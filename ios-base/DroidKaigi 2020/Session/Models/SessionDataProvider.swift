import ioscombined
import RxSwift

final class SessionDataProvider {
    func fetchSessions() -> Single<[Session]> {
        return Single.create { observer -> Disposable in
            ApiComponentKt.generateDroidKaigiApi().getSessions(callback: { response in
                let model = ResponseToModelMapperKt.toModel(__: response)
                observer(.success(model.sessions))
            }, onError: { error in
                observer(.error(KotlinError(localizedDescription: error.description())))
            })
            return Disposables.create()
        }
    }

    func fetchSessionContents() -> Single<SessionContents> {
        return Single.create { observer -> Disposable in
            ApiComponentKt.generateDroidKaigiApi().getSessions(callback: { response in
                let model = ResponseToModelMapperKt.toModel(__: response)
                observer(.success(model))
            }) { error in
                observer(.error(KotlinError(localizedDescription: error.description())))
            }
            return Disposables.create()
        }
    }
}
