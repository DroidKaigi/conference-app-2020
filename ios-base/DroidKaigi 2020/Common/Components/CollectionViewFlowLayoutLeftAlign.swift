import UIKit

/// Reference:
/// https://qiita.com/kazuhiro4949/items/03bc3d17d3826aa197c0
class CollectionViewFlowLayoutLeftAlign: UICollectionViewFlowLayout {
    override func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
        guard let attributes = super.layoutAttributesForElements(in: rect) else {
            return nil
        }

        var attributesToReturn = attributes.map { $0.copy() as! UICollectionViewLayoutAttributes }
        for (index, attr) in attributes.enumerated() where attr.representedElementCategory == .cell {
            attributesToReturn[index] = layoutAttributesForItem(at: attr.indexPath) ?? UICollectionViewLayoutAttributes()
        }
        return attributesToReturn
    }

    override func layoutAttributesForItem(at indexPath: IndexPath) -> UICollectionViewLayoutAttributes? {
        guard let currentAttributes = super.layoutAttributesForItem(at: indexPath)?.copy() as? UICollectionViewLayoutAttributes,
              let viewWidth = collectionView?.frame.width else {
                return nil
        }

        let sectionInsetsLeft = sectionInsets(at: indexPath.section).left

        guard indexPath.item > 0 else {
            currentAttributes.frame.origin.x = sectionInsetsLeft
            return currentAttributes
        }

        let prevIndexPath = IndexPath(item: indexPath.item - 1, section: indexPath.section)
        guard let prevFrame = layoutAttributesForItem(at: prevIndexPath)?.frame else {
            return nil
        }
        let validWidth = viewWidth - sectionInset.left - sectionInset.right
        let currentColumnRect = CGRect(x: sectionInsetsLeft, y: currentAttributes.frame.origin.y, width: validWidth, height: currentAttributes.frame.height)
        guard prevFrame.intersects(currentColumnRect) else {
            currentAttributes.frame.origin.x = sectionInsetsLeft
            return currentAttributes
        }

        let prevItemTailX = prevFrame.origin.x + prevFrame.width
        currentAttributes.frame.origin.x = prevItemTailX + minimumInteritemSpacing(at: indexPath.section)
        return currentAttributes
    }

    private func sectionInsets(at index: Int) -> UIEdgeInsets {
        guard
            let collectionView = collectionView,
            let delegate = collectionView.delegate as? UICollectionViewDelegateFlowLayout else {
                return self.sectionInset
        }
        return delegate.collectionView?(collectionView, layout: self, insetForSectionAt: index) ?? self.sectionInset
    }

    private func minimumInteritemSpacing(at index: Int) -> CGFloat {
        guard
            let collectionView = collectionView,
            let delegate = collectionView.delegate as? UICollectionViewDelegateFlowLayout else {
                return self.minimumInteritemSpacing
        }
        return delegate.collectionView?(collectionView, layout: self, minimumInteritemSpacingForSectionAt: index) ?? self.minimumInteritemSpacing
    }
}
