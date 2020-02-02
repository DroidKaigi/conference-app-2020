import ios_combined
import MaterialComponents
import RxCocoa
import RxSwift
import UIKit

final class SessionViewDataSource: NSObject, UICollectionViewDataSource {
    typealias Element = [AppBaseSession]
    var items: Element = []

    var onTapSpeaker: Signal<(speaker: AppSpeaker, sessions: [AppSpeechSession])> {
        return onTapSpeakerRelay.asSignal()
    }

    var onTapBookmark: Signal<(SessionCell, AppBaseSession)> {
        onTapBookmarkRelay.asSignal()
    }

    private var previousTimeString = ""
    private let disposeBag = DisposeBag()
    private let onTapSpeakerRelay = PublishRelay<(speaker: AppSpeaker, sessions: [AppSpeechSession])>()
    private let onTapBookmarkRelay = PublishRelay<(SessionCell, AppBaseSession)>()

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return items.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: SessionCell.identifier, for: indexPath) as? SessionCell else {
            return SessionCell()
        }

        let session = items[indexPath.item]

        cell.titleLabel.text = session.title?.ja ?? ""

        var speakers: [AppSpeaker] = []
        if let speechSession = session as? AppSpeechSession {
            speakers = Array(speechSession.speakers)
        }
        speakers.forEach { speaker in
            cell.addSpeakerView(imageURL: URL(string: speaker.imageUrl ?? ""), speakerName: speaker.name) { [weak self] in
                guard let self = self else { return }
                let sessions: [AppSpeechSession] = self.items.compactMap { session in
                    if let speechSession = session as? AppSpeechSession {
                        if speechSession.speakers.contains(where: { eachSessionSpeaker in eachSessionSpeaker.id == speaker.id }) {
                            return speechSession
                        }
                        return nil
                    } else {
                        return nil
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

        cell.minutesAndRoomLabel.text = session.timeRoomText

        cell.bookmarkButton.rx.tap
            .map { _ in (cell, session) }
            .bind(to: onTapBookmarkRelay)
            .disposed(by: cell.disposeBag)

        let bookmarkImage = session.isFavorited ? Asset.icBookmark.image : Asset.icBookmarkBorder.image
        cell.bookmarkButton.setImage(bookmarkImage, for: .normal)
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
