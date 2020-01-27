import ios_combined
import MaterialComponents
import RxCocoa
import RxSwift
import UIKit

final class SessionViewDataSource: NSObject, UICollectionViewDataSource {
    typealias Element = [Session]
    var items: Element = []

    var onTapSpeaker: Signal<(speaker: Speaker, sessions: [Session])> {
        return onTapSpeakerRelay.asSignal()
    }

    private var previousTimeString = ""
    private let disposeBag = DisposeBag()
    private let onTapSpeakerRelay = PublishRelay<(speaker: Speaker, sessions: [Session])>()

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
            cell.addSpeakerView(imageURL: URL(string: speaker.imageUrl ?? ""), speakerName: speaker.name) { [weak self] in
                guard let self = self else { return }
                let sessions: [Session] = self.items.filter { session in
                    if let speechSession = session as? SpeechSession {
                        return speechSession.speakers.contains { $0.id == speaker.id }
                    } else {
                        return false
                    }
                }
                self.onTapSpeakerRelay.accept((speaker: speaker, sessions: sessions))
            }
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
