//
//  SessionViewModel.swift
//  DroidKaigi 2020
//
//  Created by 伊藤凌也 on 2020/01/18.
//

import RxSwift
import RxCocoa

final class SessionViewModel {

    private let disposeBag = DisposeBag()

    private let isFocusedOnEmbeddedViewRelay = BehaviorRelay<Bool>(value: true)
    let isFocusedOnEmbeddedView: Driver<Bool>
    let toggleEmbddedView = PublishRelay<Void>()

    init() {
        self.isFocusedOnEmbeddedView = isFocusedOnEmbeddedViewRelay.asDriver()

        toggleEmbddedView
            .withLatestFrom(isFocusedOnEmbeddedViewRelay)
            .map { !$0 }
            .bind(to: isFocusedOnEmbeddedViewRelay)
            .disposed(by: disposeBag)
    }
}
