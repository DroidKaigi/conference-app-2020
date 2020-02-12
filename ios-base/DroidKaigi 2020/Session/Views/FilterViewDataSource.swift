import ioscombined
import RxCocoa
import RxSwift
import UIKit

final class FilterViewDataSource: NSObject, UICollectionViewDataSource {
    private enum FilterSections: Int, CaseIterable {
        case rooms
        case langs
        case levels
        case categories
        case langSupports
    }

    typealias Element = FilterSessionContents

    var element: Element = .empty()

    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return FilterSections.allCases.count
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        guard let filterSection = FilterSections(rawValue: section)
        else { fatalError() }
        switch filterSection {
        case .rooms:
            return element.rooms.count
        case .langs:
            return element.langs.count
        case .levels:
            return element.levels.count
        case .categories:
            return element.categories.count
        case .langSupports:
            return element.langSupports.count
        }
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: ChipCell.identifier, for: indexPath) as? ChipCell else {
            return UICollectionViewCell()
        }

        guard let filterSection = FilterSections(rawValue: indexPath.section)
        else { fatalError() }

        switch filterSection {
        case .rooms:
            cell.chipTitleLabel.text = element.rooms[indexPath.item].name.currentLangString
        case .langs:
            cell.chipTitleLabel.text = element.langs[indexPath.item].text.currentLangString
        case .levels:
            cell.chipTitleLabel.text = element.levels[indexPath.item].rawValue.currentLangString
        case .categories:
            cell.chipTitleLabel.text = element.categories[indexPath.item].name.currentLangString
        case .langSupports:
            cell.chipTitleLabel.text = element.langSupports[indexPath.item].text.currentLangString
        }

        return cell
    }

    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        guard let header = collectionView.dequeueReusableSupplementaryView(ofKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: "SectionHeader", for: indexPath) as? FilterSectionHeaderView else {
            fatalError()
        }

        guard let filterSection = FilterSections(rawValue: indexPath.section)
        else { fatalError() }
        switch filterSection {
        case .rooms:
            header.titleLabel.text = "Room"
        case .langs:
            header.titleLabel.text = "Language"
        case .levels:
            header.titleLabel.text = "Level"
        case .categories:
            header.titleLabel.text = "Category"
        case .langSupports:
            header.titleLabel.text = "Language Support"
        }

        return header
    }
}

extension FilterViewDataSource: RxCollectionViewDataSourceType, SectionedViewDataSourceType {
    func collectionView(_ collectionView: UICollectionView, observedEvent: Event<FilterViewDataSource.Element>) {
        Binder(self) { dataSource, sessionContents in
            dataSource.element = sessionContents
            collectionView.reloadData()
        }.on(observedEvent)
    }

    func model(at indexPath: IndexPath) throws -> Any {
        guard let filterSection = FilterSections(rawValue: indexPath.section)
        else { fatalError() }
        switch filterSection {
        case .rooms:
            return element.rooms[indexPath.item]
        case .langs:
            return element.langs[indexPath.item]
        case .levels:
            return element.levels[indexPath.item]
        case .categories:
            return element.categories[indexPath.item]
        case .langSupports:
            return element.langSupports[indexPath.item]
        }
    }
}
