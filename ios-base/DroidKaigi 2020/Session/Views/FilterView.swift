import Material
import UIKit

final class FilterView: CollectionView {
    override init() {
        let layout = UICollectionViewFlowLayout()
        layout.estimatedItemSize = .init(width: ChipCell.estimatedCellWidth, height: ChipCell.cellHeight)
        super.init(frame: .zero, collectionViewLayout: layout)
        register(UINib(nibName: "ChipCell", bundle: nil), forCellWithReuseIdentifier: "ChipCell")

        backgroundColor = Asset.primary.color
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
