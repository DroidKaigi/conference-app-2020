import ios_combined
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
        cell.titleLabel.text = session.title.ja
//        session.forEach { speaker in
//            cell.addSpeakerView(imageURL: URL(string: speaker.profilePicture ?? ""), speakerName: speaker.fullName ?? "")
//        }

        if previousTimeString != session.startTimeText {
            cell.timeLabel.text = session.startTimeText
        } else {
            cell.timeLabel.text = ""
        }
        previousTimeString = session.startTimeText
        
        cell.minutesAndRoomLabel.text = "\(session.timeInMinutes)min / \(session.room.name.ja)"

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
