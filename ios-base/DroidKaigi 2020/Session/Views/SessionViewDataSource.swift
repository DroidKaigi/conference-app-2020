import ios_combined
import MaterialComponents
import RxCocoa
import RxSwift
import UIKit

final class SessionViewDataSource: NSObject, UICollectionViewDataSource {
    typealias Element = [Session]
    var items: Element = []

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

        var speakers: [Speaker] = []
        if let speechSession = session as? SpeechSession {
            speakers = speechSession.speakers
        }
        speakers.forEach { speaker in
            cell.addSpeakerView(imageURL: URL(string: speaker.imageUrl ?? ""), speakerName: speaker.name)
        }

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
    func collectionView(_ collectionView: UICollectionView, observedEvent: Event<SessionViewDataSource.Element>) {
        Binder(self) { dataSource, items in
            dataSource.items = items
            collectionView.reloadData()
        }.on(observedEvent)
    }

    func model(at indexPath: IndexPath) throws -> Any {
        items[indexPath.item]
    }
}
