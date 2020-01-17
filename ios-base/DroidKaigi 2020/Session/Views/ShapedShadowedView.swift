//
//  ShapedShadowedView.swift
//  DroidKaigi 2020
//
//  Created by 伊藤凌也 on 2020/01/17.
//

import UIKit

import MaterialComponents

class ShapedShadowedView: MDCShapedView {

    override class var layerClass: Swift.AnyClass {
        get {
            return MDCShapedShadowLayer.self
        }
    }

    override init(frame: CGRect, shapeGenerator: MDCShapeGenerating?) {
        super.init(frame: frame, shapeGenerator: shapeGenerator)
        self.layer.shadowOffset = CGSize(width: 0.0, height: 0.0)
        self.layer.shadowRadius = 4.0
        self.layer.shadowOpacity = 0.8
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        self.layer.shadowOffset = CGSize(width: 0.0, height: 0.0)
        self.layer.shadowRadius = 4.0
        self.layer.shadowOpacity = 0.8
    }
}

