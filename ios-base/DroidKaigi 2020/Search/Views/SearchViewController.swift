import Material
import UIKit
import RxCocoa
import RxSwift

final class SearchViewController: SearchBarController {

    private let disposeBag = DisposeBag()

    var backButton: IconButton!

    override func prepare() {
        super.prepare()
        prepareSearchBar()
        navigationController?.navigationBar.isHidden = true

        backButton.rx.tap
            .bind(to: Binder(self) { me, _ in
                me.navigationController?.popViewController(animated: true)
            }).disposed(by: disposeBag)
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
    }
}

extension SearchViewController: SearchBarDelegate {

}
