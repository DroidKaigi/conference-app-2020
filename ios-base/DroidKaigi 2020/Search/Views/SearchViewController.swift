import Material
import RxCocoa
import RxSwift
import UIKit

final class SearchViewController: SearchBarController {
    private let disposeBag = DisposeBag()

    var backButton: IconButton!

    override func prepare() {
        super.prepare()
        prepareSearchBar()

        backButton.rx.tap
            .bind(to: Binder(self) { me, _ in
                me.navigationController?.popViewController(animated: true)
            }).disposed(by: disposeBag)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.navigationBar.isHidden = true
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        navigationController?.navigationBar.isHidden = false
    }

    private func prepareSearchBar() {
        let backImage = Asset.icBack.image
            .withRenderingMode(.alwaysTemplate)
            .tint(with: Asset.primary.color)
        backButton = IconButton(image: backImage)
        searchBar.leftViews = [backButton]

        let clearImage = searchBar.clearButton.image
        searchBar.clearButton.image = clearImage?
            .withRenderingMode(.alwaysTemplate)
            .tint(with: Asset.primary.color)
        searchBar.placeholder = L10n.search
    }
}
