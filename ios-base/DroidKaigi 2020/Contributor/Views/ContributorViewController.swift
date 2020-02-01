import UIKit

final class ContributorViewController: UIViewController {
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.navigationBar.barTintColor = ApplicationScheme.shared.colorScheme.surfaceColor
        navigationController?.navigationBar.tintColor = ApplicationScheme.shared.colorScheme.onSurfaceColor
    }
}

extension ContributorViewController {
    static func instantiate() -> ContributorViewController {
        let storyboard = UIStoryboard(name: String(describing: self), bundle: .main)
        guard let viewController = storyboard.instantiateInitialViewController() as? ContributorViewController else {
            fatalError()
        }
        return viewController
    }
}
