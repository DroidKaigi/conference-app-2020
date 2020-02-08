import MaterialComponents
import UIKit

final class ApplicationScheme: NSObject {
    static let shared: ApplicationScheme = ApplicationScheme()

    public let colorScheme: MDCColorScheming = {
        return ApplicationScheme.semanticColorScheme
    }()

    public let buttonScheme: MDCContainerScheme = {
        let scheme = MDCContainerScheme()
        scheme.colorScheme = ApplicationScheme.semanticColorScheme
        scheme.typographyScheme = ApplicationScheme._typographyScheme
        return scheme
    }()

    public let shapeScheme: MDCShapeScheming = {
        let scheme = MDCShapeScheme()
        scheme.largeComponentShape = MDCShapeCategory(cornersWith: .cut, andSize: 20)
        return scheme
    }()

    public let typographyScheme: MDCTypographyScheming = {
        return ApplicationScheme._typographyScheme
    }()

    private static let _typographyScheme: MDCTypographyScheme = {
        let scheme = MDCTypographyScheme(defaults: .material201804)
        return scheme
    }()

    private static let semanticColorScheme: MDCSemanticColorScheme = {
        let scheme = MDCSemanticColorScheme(defaults: .material201804)

        scheme.primaryColor = UIColor(named: "Primary")!
        scheme.onPrimaryColor = UIColor(named: "OnPrimary")!
        scheme.secondaryColor = UIColor(named: "Secondary")!
        scheme.surfaceColor = UIColor(named: "Surface")!
        scheme.onSurfaceColor = UIColor(named: "OnSurface")!
        return scheme
    }()
}
