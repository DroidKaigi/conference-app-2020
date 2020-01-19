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

    typealias Elememt = [Session]
    var items: Elememt = []

    private var previousTimeString = ""

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return items.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: SessionCell.identifier, for: indexPath) as? SessionCell else {
            return SessionCell()
        }

        let session = items[indexPath.item]
        cell.titleLabel.text = session.response.title?.en
        session.speakers.forEach { speaker in
            cell.addSpeakerView(imageURL: URL(string: speaker.profilePicture ?? ""), speakerName: speaker.fullName ?? "")
        }

        let calendar = Calendar.current
        if let startsAt = session.startsAt {
            let hour = calendar.component(.hour, from: startsAt)
            let minute = String(format: "%02d", arguments: [calendar.component(.minute, from: startsAt)])
            let timeString = "\(hour):\(minute)"

            if previousTimeString != timeString {
                cell.timeLabel.text = timeString
            } else {
                cell.timeLabel.text = ""
            }
            previousTimeString = timeString
        }
        cell.minutesAndRoomLabel.text = "\(session.minutes!)min / \(session.room?.name?.ja ?? "")"

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
