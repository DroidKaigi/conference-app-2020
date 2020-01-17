//
//  ApplicationScheme.swift
//  DroidKaigi 2020
//
//  Created by 伊藤凌也 on 2020/01/17.
//

import UIKit
import MaterialComponents

final class ApplicationScheme: NSObject {

    private static var singleton = ApplicationScheme()

    static var shared: ApplicationScheme {
        return singleton
    }

    public let colorScheme: MDCColorScheming = {
        let scheme = MDCSemanticColorScheme(defaults: .material201804)
        
        scheme.primaryColor = UIColor(hex: "041E42")
        scheme.primaryColorVariant =
            UIColor(red: 68.0/255.0, green: 44.0/255.0, blue: 46.0/255.0, alpha: 1.0)
        scheme.onPrimaryColor = .white
        scheme.secondaryColor = UIColor(hex: "041E42")
        scheme.onSecondaryColor =
            UIColor(red: 68.0/255.0, green: 44.0/255.0, blue: 46.0/255.0, alpha: 1.0)
        scheme.surfaceColor = UIColor(hex: "041E42")
        scheme.onSurfaceColor =
            UIColor(red: 68.0/255.0, green: 44.0/255.0, blue: 46.0/255.0, alpha: 1.0)
        scheme.backgroundColor =
            UIColor(red: 255.0/255.0, green: 255.0/255.0, blue: 255.0/255.0, alpha: 1.0)
        scheme.onBackgroundColor =
            UIColor(red: 68.0/255.0, green: 44.0/255.0, blue: 46.0/255.0, alpha: 1.0)
        scheme.errorColor =
            UIColor(red: 197.0/255.0, green: 3.0/255.0, blue: 43.0/255.0, alpha: 1.0)
        return scheme
    }()

    public let shapeScheme: MDCShapeScheming = {
        let scheme = MDCShapeScheme()
        scheme.largeComponentShape = MDCShapeCategory(cornersWith: .cut, andSize: 20)
        return scheme
    }()

}
