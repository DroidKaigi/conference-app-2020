import ioscombined
import RxSwift

protocol ContributorDataProviderProtocol {
    func fetchContributors() -> Single<[ContributorIndex]>
}

final class ContributorDataProvider: ContributorDataProviderProtocol {
    func fetchContributors() -> Single<[ContributorIndex]> {
        return Single.create { observer -> Disposable in
            ApiComponentKt.generateDroidKaigiApi().getContributorList(callback: { response in
                let model = ContributorResponseToModelMapperKt.toModel(response)
                observer(.success(model))
            }) { error in
                observer(.error(KotlinError(localizedDescription: error.description())))
            }
            return Disposables.create()
        }
    }
}
