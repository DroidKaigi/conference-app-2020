import ios_combined
import MaterialComponents
import Nuke
import RxCocoa
import RxSwift
import UIKit

final class SponsorViewDataSource: NSObject, UICollectionViewDataSource {
    typealias Element = [SponsorCategory]
    var items: Element = []

    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return items.count
    }

    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        if kind == UICollectionView.elementKindSectionHeader,
            let view = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: SponsorCategoryHeaderView.identifier, for: indexPath) as? SponsorCategoryHeaderView {
            view.titleLabel.text = items[indexPath.section].category.title.uppercased()
            return view
        } else if kind == UICollectionView.elementKindSectionFooter,
            let view = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: SponsorDividerView.identifier, for: indexPath) as? SponsorDividerView {
            return view
        } else {
            return UICollectionReusableView()
        }
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return items[section].sponsors.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: SponsorCell.identifier, for: indexPath) as? SponsorCell else {
            return SponsorCell()
        }

        let sponsor = items[indexPath.section].sponsors[indexPath.item]

        cell.cornerRadius = 8
        cell.setShadowElevation(.cardResting, for: .normal)

        let url = URL(string: sponsor.company.logoUrl)!
        let options = ImageLoadingOptions(transition: .fadeIn(duration: 0.3))
        Nuke.loadImage(with: url, options: options, into: cell.imageView)

        return cell
    }
}

extension SponsorViewDataSource: RxCollectionViewDataSourceType, SectionedViewDataSourceType {
    func collectionView(_ collectionView: UICollectionView, observedEvent: Event<SponsorViewDataSource.Element>) {
        Binder(self) { dataSource, items in
            dataSource.items = items
            collectionView.reloadData()
        }.on(observedEvent)
    }

    func model(at indexPath: IndexPath) throws -> Any {
        items[indexPath.section].sponsors[indexPath.item]
    }
}

extension SponsorViewDataSource: UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        guard let flowLayout = collectionViewLayout as? UICollectionViewFlowLayout else {
            return .zero
        }

        let width: CGFloat
        let height: CGFloat

        let category = items[indexPath.section].category
        switch category {
        case .platinum:
            width = collectionView.frame.width
                - flowLayout.sectionInset.left
                - flowLayout.sectionInset.right
            height = 112
        default: // .gold, .supporter, .committeeSupport
            width = collectionView.frame.width / 2
                - flowLayout.sectionInset.left
                - flowLayout.minimumInteritemSpacing / 2
            if category == .gold {
                height = 112
            } else {
                height = 72
            }
        }

        return CGSize(width: width, height: height)
    }
}
