import ios_combined
import RxSwift

final class SponsorDataProvider {
    func fetchSponsors() -> Single<[SponsorCategory]> {
        return Single.create { observer -> Disposable in
            ApiComponentKt.generateDroidKaigiApi().getSponsors(callback: { response in
                let model = SponsorListResponseToModelMapperKt.toModel(__: response)
                observer(.success(model))
            }) { error in
                observer(.error(KotlinError(localizedDescription: error.description())))
            }
            return Disposables.create()
        }
    }
}
