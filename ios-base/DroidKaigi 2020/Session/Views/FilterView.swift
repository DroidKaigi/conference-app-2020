import Material
import UIKit

final class FilterView: CollectionView {
    override init() {
        let layout = UICollectionViewFlowLayout()
        layout.estimatedItemSize = .init(width: ChipCell.estimatedCellWidth, height: ChipCell.cellHeight)
        layout.headerReferenceSize = .init(width: UIScreen.main.bounds.width, height: 60)
        super.init(frame: .zero, collectionViewLayout: layout)
        register(UINib(nibName: "FilterSectionHeaderView", bundle: nil), forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: "SectionHeader")
        register(UINib(nibName: "ChipCell", bundle: nil), forCellWithReuseIdentifier: "ChipCell")

        backgroundColor = Asset.primary.color
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
