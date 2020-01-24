import UIKit

extension UIImageView {
    class Cache {
        private var cache = [String: UIImage]()

        fileprivate func setImage(_ image: UIImage, for key: URL) {
            cache[key.absoluteString] = image
        }

        fileprivate func image(for key: URL) -> UIImage? {
            return cache[key.absoluteString]
        }
    }

    private static let imageLoadingQueue = OperationQueue()

    private static var imageLoadingOperationKey = 0

    private var imageLoadingOperation: Operation? {
        get {
            return objc_getAssociatedObject(
                self,
                &UIImageView.imageLoadingOperationKey
            ) as? Operation
        }
        set {
            objc_setAssociatedObject(
                self,
                &UIImageView.imageLoadingOperationKey,
                newValue,
                .OBJC_ASSOCIATION_RETAIN_NONATOMIC
            )
        }
    }

    func loadImage(url: URL?, using cache: Cache? = nil) {
        image = nil
        imageLoadingOperation?.cancel()
        imageLoadingOperation = nil

        guard let url = url else {
            return
        }

        if let cachedImage = cache?.image(for: url) {
            image = cachedImage
            return
        }

        let operation = BlockOperation()
        operation.addExecutionBlock { [weak self, weak operation] in
            if let op = operation, op.isCancelled {
                return
            }
            do {
                let data = try Data(contentsOf: url)
                if let image = UIImage(data: data) {
                    DispatchQueue.main.async {
                        cache?.setImage(image, for: url)
                        if let op = operation, !op.isCancelled {
                            self?.image = image
                        }
                    }
                }
            } catch {
                print(error.localizedDescription)
            }
        }
        UIImageView.imageLoadingQueue.addOperation(operation)
        imageLoadingOperation = operation
    }
}
