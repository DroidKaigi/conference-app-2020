//
//  SessionDataProvider.swift
//  DroidKaigi 2020
//
//  Created by 伊藤凌也 on 2020/01/18.
//

import api
import RxSwift

final class SessionDataProvider {
    func fetchSessions() -> Observable<Response> {
//        let api = ApiComponentKt.generateDroidKaigiApi()
        return Observable.create { observer -> Disposable in
            ApiComponentKt.generateDroidKaigiApi().getSessions(callback: { response in
                observer.onNext(response)
            }) { error in
                observer.onError(KotlinError(localizedDescription: error.description()))
            }
            return Disposables.create()
        }
    }
}
