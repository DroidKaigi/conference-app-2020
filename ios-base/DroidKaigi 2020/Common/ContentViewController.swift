import RxCocoa
import RxSwift
import UIKit

// this is base contentViewController in NavigationDrawerController
class ContentViewController: UIViewController {
    private let disposeBag = DisposeBag()
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setUpAppBar()
    }

    private func setUpAppBar() {
        let menuImage = Asset.icMenu.image
        let templateMenuImage = menuImage.withRenderingMode(.alwaysTemplate)
        let menuItem = UIBarButtonItem(image: templateMenuImage,
                                       style: .plain,
                                       target: self,
                                       action: nil)
        let titleItem = UIBarButtonItem(title: title,
                                        style: .plain,
                                        target: nil,
                                        action: nil)
        titleItem.setTitleTextAttributes([.foregroundColor: UIColor.black], for: .disabled)
        titleItem.isEnabled = false
        navigationItem.leftBarButtonItems = [menuItem, titleItem]

        navigationController?.navigationBar.barTintColor = ApplicationScheme.shared.colorScheme.backgroundColor
        navigationController?.navigationBar.tintColor = ApplicationScheme.shared.colorScheme.onBackgroundColor

        menuItem.rx.tap
            .bind(to: Binder(self) { me, _ in
                me.navigationDrawerController?.toggleLeftView()
            }).disposed(by: disposeBag)
    }
}
