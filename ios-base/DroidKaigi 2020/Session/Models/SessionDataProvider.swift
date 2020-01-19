import ios_combined
import RxSwift

final class SessionDataProvider {
    func fetchSessions() -> Observable<[Session]> {
        return Observable.create { observer -> Disposable in
            ApiComponentKt.generateDroidKaigiApi().getSessions(callback: { response in
                let model = ResponseToModelMapperKt.toModel(response)
                observer.onNext(model.sessions)
            }) { error in
                observer.onError(KotlinError(localizedDescription: error.description()))
            }
            return Disposables.create()
        }
    }
}
