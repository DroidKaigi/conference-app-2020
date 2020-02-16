import ioscombined
import RxCocoa
import RxSwift
import UIKit

final class SearchContentsDataSource: NSObject, UICollectionViewDataSource {
    private enum Sections: Int, CaseIterable {
        case speaker
        case session
    }

    typealias Element = SearchResult
    private var element: Element = .empty()

    private let disposeBag = DisposeBag()

    var onTapSpeaker: Signal<(speaker: Speaker, sessions: [Session])> {
        return onTapSpeakerRelay.asSignal()
    }

    var onTapBookmark: Signal<Session> {
        onTapBookmarkRelay.asSignal()
    }

    private let onTapSpeakerRelay = PublishRelay<(speaker: Speaker, sessions: [Session])>()
    private let onTapBookmarkRelay = PublishRelay<Session>()

    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return Sections.allCases.count
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        guard let sectionCase = Sections(rawValue: section) else {
            return 0
        }
        switch sectionCase {
        case .session:
            return element.sessions.count
        case .speaker:
            return element.speakers.count
        }
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let sectionCase = Sections(rawValue: indexPath.section) else {
            return UICollectionViewCell()
        }
        switch sectionCase {
        case .session:
            guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: SessionCell.identifier, for: indexPath) as? SessionCell else {
                return UICollectionViewCell()
            }
            let session = element.sessions[indexPath.item]
            cell.titleLabel.text = session.title.currentLangString
            var speakers: [Speaker] = []
            if let speechSession = session as? SpeechSession {
                speakers = speechSession.speakers
            }
            speakers.forEach { speaker in
                cell.addSpeakerView(imageURL: URL(string: speaker.imageUrl ?? ""), speakerName: speaker.name) { [weak self] in
                    guard let self = self else { return }
                    let sessions: [SpeechSession] = self.element.sessions
                        .compactMap { $0 as? SpeechSession }
                        .filter { speechSession in
                            speechSession.speakers.contains(where: { $0.id.id == speaker.id.id })
                        }
                    self.onTapSpeakerRelay.accept((speaker: speaker, sessions: sessions))
                }
            }

            cell.dateLabelInFirstFavoriteSession.text = session.startMonthAndDayText
            cell.timeLabel.isHidden = true

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

            cell.titleLeftConstraint.constant = 0

            if let speechSession = session as? SpeechSession, let message = speechSession.message {
                cell.sessionMessage = message.currentLangString
            } else {
                cell.sessionMessage = ""
            }

            return cell
        case .speaker:
            guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: ResultSpeakerCell.identifier, for: indexPath) as? ResultSpeakerCell else {
                return UICollectionViewCell()
            }
            cell.configure(speaker: element.speakers[indexPath.item])
            return cell
        }
    }

    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        guard
            let header = collectionView.dequeueReusableSupplementaryView(ofKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: "SearchSectionHeaderView", for: indexPath) as? SearchSectionHeaderView,
            let sectionCase = Sections(rawValue: indexPath.section)
        else {
            return UICollectionReusableView()
        }

        switch sectionCase {
        case .session:
            header.titleLabel.text = L10n.session
        case .speaker:
            header.titleLabel.text = L10n.speaker
        }
        return header
    }
}

extension SearchContentsDataSource: RxCollectionViewDataSourceType, SectionedViewDataSourceType {
    func model(at indexPath: IndexPath) throws -> Any {
        guard let sectionCase = Sections(rawValue: indexPath.section) else {
            fatalError()
        }
        switch sectionCase {
        case .session:
            return element.sessions[indexPath.item]
        case .speaker:
            return element.speakers[indexPath.item]
        }
    }

    func collectionView(_ collectionView: UICollectionView, observedEvent: Event<SearchContentsDataSource.Element>) {
        Binder(self) { _, element in
            self.element = element
            collectionView.reloadData()
        }.on(observedEvent)
    }
}
