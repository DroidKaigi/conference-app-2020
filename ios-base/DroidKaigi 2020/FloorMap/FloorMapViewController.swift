import ioscombined
import UIKit

final class FloorMapViewController: ContentViewController {
    static func instantiate() -> FloorMapViewController {
        guard let viewController = UIStoryboard(name: "FloorMapViewController", bundle: .main).instantiateInitialViewController() as? FloorMapViewController else { fatalError() }
        return viewController
    }

    @IBOutlet var imageView: UIImageView!

    init() {
        super.init(nibName: nil, bundle: nil)
        title = L10n.floorMaps
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        title = L10n.floorMaps
    }

    override func viewDidLoad() {
        super.viewDidLoad()
    }
}
