import ios_combined
import RxSwift

final class ContributorDataProvider {
    func fetchContributors() -> Single<ContributorResponse> {
        return Single.create { observer -> Disposable in
            ApiComponentKt.generateDroidKaigiApi().getContributorList(callback: { response in
                observer(.success(response))
            }) { error in
                observer(.error(KotlinError(localizedDescription: error.description())))
            }
            return Disposables.create()
        }
    }
}
