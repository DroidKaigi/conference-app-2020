//
//  SessionViewDataSource.swift
//  DroidKaigi 2020
//
//  Created by 伊藤凌也 on 2020/01/18.
//

import UIKit
import RxSwift
import RxCocoa
import MaterialComponents

final class SessionViewDataSource: NSObject, UICollectionViewDataSource {

    typealias Elememt = [String]
    var items: Elememt

    init(items: Elememt) {
        self.items = items
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return items.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "cell", for: indexPath) as? MDCSelfSizingStereoCell else {
            return MDCSelfSizingStereoCell()
        }

        cell.titleLabel.text = items[indexPath.item]

        return cell
    }


}

extension SessionViewDataSource: RxCollectionViewDataSourceType, SectionedViewDataSourceType {
    func collectionView(_ collectionView: UICollectionView, observedEvent: Event<SessionViewDataSource.Elememt>) {
        Binder(self) { dataSource, items in
            dataSource.items = items
            collectionView.reloadData()
        }.on(observedEvent)
    }

    func model(at indexPath: IndexPath) throws -> Any {
        items[indexPath.item]
    }
}
