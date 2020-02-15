import ioscombined
import Nuke
import RxCocoa
import RxSwift
import UIKit

final class ContributorViewDataSource: NSObject, UICollectionViewDataSource {
    typealias Element = [ContributorIndex]

    private var items: Element = []

    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return items.count
    }

    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        switch kind {
        case UICollectionView.elementKindSectionHeader:
            guard let view = collectionView.dequeueReusableSupplementaryView(
                ofKind: kind,
                withReuseIdentifier: ContributorIndexHeaderView.identifier,
                for: indexPath
            ) as? ContributorIndexHeaderView else {
                preconditionFailure()
            }
            view.indexLabel.text = items[indexPath.section].index.uppercased()
            // for overlapping cells
            view.frame.size = CGSize(width: UIScreen.main.bounds.width, height: ContributorCell.rowHeight)
            view.clipsToBounds = false
            return view

        default:
            preconditionFailure()
        }
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return items[section].contributors.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: ContributorCell.identifier, for: indexPath) as? ContributorCell else {
            preconditionFailure()
        }

        let contributor = items[indexPath.section].contributors[indexPath.item]

        if let url = URL(string: contributor.iconUrl) {
            let options = ImageLoadingOptions(transition: .fadeIn(duration: 0.3))
            Nuke.loadImage(with: url, options: options, into: cell.iconImageView)
        }

        cell.nameLabel.text = contributor.name

        return cell
    }
}

extension ContributorViewDataSource: RxCollectionViewDataSourceType, SectionedViewDataSourceType {
    func collectionView(_ collectionView: UICollectionView, observedEvent: Event<ContributorViewDataSource.Element>) {
        Binder(self) { dataSource, items in
            dataSource.items = items
            collectionView.reloadData()
        }.on(observedEvent)
    }

    func model(at indexPath: IndexPath) throws -> Any {
        return items[indexPath.section].contributors[indexPath.item]
    }
}
