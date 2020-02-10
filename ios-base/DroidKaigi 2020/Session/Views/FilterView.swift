import Material
import UIKit

final class FilterView: UIView {
    var headerView: FilterHeaderView!
    var collectionView: CollectionView!

    init() {
        super.init(frame: .zero)

        backgroundColor = Asset.primary.color

        setupFilterHeaderView()
        setupCollectionView()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupFilterHeaderView() {
        headerView = FilterHeaderView(frame: .zero)
        addSubview(headerView)
        headerView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            headerView.topAnchor.constraint(equalTo: topAnchor),
            headerView.leftAnchor.constraint(equalTo: leftAnchor),
            headerView.rightAnchor.constraint(equalTo: rightAnchor),
            headerView.heightAnchor.constraint(equalToConstant: 94),
        ])
    }

    private func setupCollectionView() {
        let layout = CollectionViewFlowLayoutLeftAlign()
        layout.estimatedItemSize = .init(width: ChipCell.estimatedCellWidth, height: ChipCell.cellHeight)
        layout.headerReferenceSize = .init(width: UIScreen.main.bounds.width, height: 60)
        collectionView = CollectionView(frame: .zero, collectionViewLayout: layout)
        collectionView.register(UINib(nibName: "FilterSectionHeaderView", bundle: nil), forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: "SectionHeader")
        collectionView.register(UINib(nibName: "ChipCell", bundle: nil), forCellWithReuseIdentifier: "ChipCell")
        collectionView.contentInset = .init(top: 0, left: 24, bottom: 100, right: 24)
        addSubview(collectionView)
        collectionView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            collectionView.topAnchor.constraint(equalTo: headerView.bottomAnchor),
            collectionView.leftAnchor.constraint(equalTo: leftAnchor),
            collectionView.rightAnchor.constraint(equalTo: rightAnchor),
            collectionView.bottomAnchor.constraint(equalTo: bottomAnchor),
        ])
        collectionView.backgroundColor = .clear
        collectionView.allowsMultipleSelection = true
    }
}
