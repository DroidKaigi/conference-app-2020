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
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        loadMap()
    }
}

// MARK: - Private functions

private extension FloorMapViewController {
    /// This method caches the floor map image in LRU memory when loading from server.
    func loadMap() {
        let urlString: String = "https://api.droidkaigi.jp/images/2020/map.png"
        if let url = URL(string: urlString) {
            let width = imageView.image?.size.width
            let height = imageView.image?.size.height

            let request = ImageRequest(
                url: url,
                processors: [ImageProcessor.Resize(size: CGSize(width: width!, height: height!))] // Set target size in pixels
            )

            if let cachedImage = ImageCache.shared[request] {
                imageView.image = cachedImage
            } else {
                ImagePipeline.shared.loadImage(with: url) { result in
                    switch result {
                    case let .success(response):
                        ImageCache.shared[request] = response.image // Set cache
                        self.imageView.image = ImageCache.shared[request]
                    case .failure:
                        self.imageView.image = Asset.map.image // The set image is applied when reading from the server fails
                    }
                }
            }
        }
    }
}
