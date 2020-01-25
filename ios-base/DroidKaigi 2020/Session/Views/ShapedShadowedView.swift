import UIKit

import MaterialComponents

class ShapedShadowedView: MDCShapedView {
    override class var layerClass: Swift.AnyClass {
        return MDCShapedShadowLayer.self
    }

    override init(frame: CGRect, shapeGenerator: MDCShapeGenerating?) {
        super.init(frame: frame, shapeGenerator: shapeGenerator)
        layer.shadowOffset = CGSize(width: 0.0, height: 0.0)
        layer.shadowRadius = 4.0
        layer.shadowOpacity = 0.8
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        layer.shadowOffset = CGSize(width: 0.0, height: 0.0)
        layer.shadowRadius = 4.0
        layer.shadowOpacity = 0.8
    }
}
