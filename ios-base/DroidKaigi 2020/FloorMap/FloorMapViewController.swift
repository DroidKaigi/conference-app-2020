import ioscombined
import Nuke
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
        loadMap()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
}

// MARK: - Private functions

private extension FloorMapViewController {
    func loadMap() {
        let width = imageView.image?.size.width
        let height = imageView.image?.size.height

        let urlString: String = "https://api.droidkaigi.jp/images/2020/map.png"
        if let url = URL(string: urlString) {
            let request = ImageRequest(
                url: url,
                processors: [ImageProcessor.Resize(size: CGSize(width: width!, height: height!))] // Set target size in pixels
            )
            let options = ImageLoadingOptions(
                failureImage: UIImage(named: "map")) // The set image is applied when reading from the server fails
            Nuke.loadImage(with: request, options: options, into: imageView)
        }
    }
}
