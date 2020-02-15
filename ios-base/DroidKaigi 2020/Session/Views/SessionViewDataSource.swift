import ioscombined
import MaterialComponents
import RxCocoa
import RxSwift
import UIKit

final class SessionViewDataSource: NSObject, UICollectionViewDataSource {
    typealias Element = [Session]
    var items: Element = []
    let type: SessionViewControllerType

    var onTapSpeaker: Signal<(speaker: Speaker, sessions: [Session])> {
        return onTapSpeakerRelay.asSignal()
    }

    var onTapBookmark: Signal<Session> {
        onTapBookmarkRelay.asSignal()
    }

    private var previousTimeString = ""
    private var previousDayString = ""
    private let disposeBag = DisposeBag()
    private let onTapSpeakerRelay = PublishRelay<(speaker: Speaker, sessions: [Session])>()
    private let onTapBookmarkRelay = PublishRelay<Session>()

    init(type: SessionViewControllerType) {
        self.type = type
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return items.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: SessionCell.identifier, for: indexPath) as? SessionCell else {
            return SessionCell()
        }

        let session = items[indexPath.item]

        cell.titleLabel.text = session.title.currentLangString

        var speakers: [Speaker] = []
        if let speechSession = session as? SpeechSession {
            speakers = speechSession.speakers
        }
        speakers.forEach { speaker in
            cell.addSpeakerView(imageURL: URL(string: speaker.imageUrl ?? ""), speakerName: speaker.name) { [weak self] in
                guard let self = self else { return }
                let sessions: [SpeechSession] = self.items.compactMap { $0 as? SpeechSession }.filter { speechSession in
                    speechSession.speakers.contains(where: { $0.id.id == speaker.id.id })
                }
                self.onTapSpeakerRelay.accept((speaker: speaker, sessions: sessions))
            }
        }

        cell.dateLabelInFirstFavoriteSession.text = session.startMonthAndDayText

        if indexPath.item > 0 {
            previousDayString = items[indexPath.item - 1].startMonthAndDayText
            previousTimeString = items[indexPath.item - 1].startTimeText
        } else {
            previousDayString = ""
            previousTimeString = ""
        }

        if previousTimeString != session.startTimeText {
            if type == .myPlan, previousDayString != session.startMonthAndDayText {
                cell.dateLabelInFirstFavoriteSession.isHidden = false
            }
            cell.timeLabel.text = session.startTimeText
        } else {
            cell.timeLabel.text = ""
        }

        cell.minutesAndRoomLabel.text = session.timeRoomText

        cell.bookmarkButton.rx.tap
            .map { _ in session }
            .bind(to: onTapBookmarkRelay)
            .disposed(by: cell.disposeBag)

        if session.isOnGoing {
            cell.liveBadge.isHidden = false
        } else {
            cell.liveBadge.isHidden = true
        }

        cell.bookmarkButton.isSelected = session.isFavorited
        cell.descriptionText = type == .event ? session.desc : ""

        if let speechSession = session as? SpeechSession, let message = speechSession.message {
            cell.sessionMessage = message.currentLangString
        } else {
            cell.sessionMessage = ""
        }

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
