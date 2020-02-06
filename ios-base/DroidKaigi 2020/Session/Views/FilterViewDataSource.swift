import ios_combined
import RxCocoa
import RxSwift
import UIKit

final class FilterViewDataSource: NSObject, UICollectionViewDataSource {
    private enum FilterSections: Int {
        case rooms
        case langs
        case levels
        case categories
    }

    typealias Element = SessionContents

    var rooms: [Room] = []
    var langs: [Lang] = []
    var levels: [Level] = []
    var categories: [ios_combined.Category] = []

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {

        guard let filterSection = FilterSections(rawValue: section)
        else { fatalError() }
        switch filterSection {
        case .rooms:
            return rooms.count
        case .langs:
            return langs.count
        case .levels:
            return levels.count
        case .categories:
            return categories.count
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
            cell.chipTitleLabel.text = rooms[indexPath.item].name.en
        case .langs:
            cell.chipTitleLabel.text = langs[indexPath.item].text.en
        case .levels:
            cell.chipTitleLabel.text = levels[indexPath.item].name
        case .categories:
            cell.chipTitleLabel.text = categories[indexPath.item].name.en
        }

        return cell
    }
}

extension FilterViewDataSource: RxCollectionViewDataSourceType, SectionedViewDataSourceType {
    func collectionView(_ collectionView: UICollectionView, observedEvent: Event<FilterViewDataSource.Element>) {
        Binder(self) { dataSource, sessionContents in
            dataSource.rooms = sessionContents.rooms
            dataSource.langs = sessionContents.langs
            dataSource.levels = sessionContents.levels
            dataSource.categories = sessionContents.category
        }.on(observedEvent)
    }

    func model(at indexPath: IndexPath) throws -> Any {
        guard let filterSection = FilterSections(rawValue: indexPath.section)
        else { fatalError() }
        switch filterSection {
        case .rooms:
            return rooms[indexPath.item]
        case .langs:
            return langs[indexPath.item]
        case .levels:
            return levels[indexPath.item]
        case .categories:
            return categories[indexPath.item]
        }
    }
}
