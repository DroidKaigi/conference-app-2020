import Material
import MaterialComponents
import RxCocoa
import RxSwift
import UIKit

protocol FilterViewControllerDelegate: AnyObject {
    func shouldChangeTab(index: Int)
}

final class FilterViewController: UIViewController {
    private let disposeBag = DisposeBag()

    private let tabBar = MDCTabBar()

    private var embeddedView: UIView!
    private var embeddedViewController: SessionPageViewController?
    private var filterView: FilterView!
    private let filterViewModel = FilterViewModel()

    private let embeddedViewAnimator = UIViewPropertyAnimator(duration: 0.8, curve: .easeInOut)

    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = ApplicationScheme.shared.colorScheme.primaryColor
        setUpAppBar()
        setUpTabBar()
        setupFilterView()
        setUpContainerView()
        bindToViewModel()
        view.bringSubviewToFront(embeddedView)
        filterViewModel.viewDidLoad()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        let embeddedFrame = frameForEmbeddedController()
        embeddedView?.frame = embeddedFrame
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.navigationBar.barTintColor = ApplicationScheme.shared.colorScheme.primaryColor
        navigationController?.navigationBar.tintColor = ApplicationScheme.shared.colorScheme.onPrimaryColor
    }

    private func frameForEmbeddedController() -> CGRect {
        var embeddedFrame = view.bounds
        var insetHeader = UIEdgeInsets()
        let bottomMargin: CGFloat = 24
        insetHeader.top = tabBar.bounds.maxY + bottomMargin
        embeddedFrame = embeddedFrame.inset(by: insetHeader)

        if embeddedView == nil {
            embeddedFrame.origin.y = view.bounds.maxY
        }

        return embeddedFrame
    }

    private func setUpAppBar() {
        let menuImage = Asset.icMenu.image
        let templateMenuImage = menuImage.withRenderingMode(.alwaysTemplate)
        let menuItem = UIBarButtonItem(image: templateMenuImage,
                                       style: .plain,
                                       target: self,
                                       action: nil)
        let logoImage = Asset.logo.image
        let templateLogoImage = logoImage.withRenderingMode(.alwaysOriginal)
        let logoItem = UIBarButtonItem(image: templateLogoImage, style: .plain, target: nil, action: nil)
        let searchImage = Asset.icSearch.image
        let templateSearchImage = searchImage.withRenderingMode(.alwaysTemplate)
        let searchItem = UIBarButtonItem(image: templateSearchImage,
                                         style: .plain,
                                         target: self,
                                         action: nil)
        navigationItem.leftBarButtonItems = [menuItem, logoItem]
        navigationItem.rightBarButtonItems = [searchItem]
        navigationController?.navigationBar.shadowImage = UIImage()
        navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .plain, target: nil, action: nil)
        edgesForExtendedLayout = []

        menuItem.rx.tap
            .bind(to: Binder(self) { me, _ in
                me.navigationDrawerController?.toggleLeftView()
            }).disposed(by: disposeBag)

        searchItem.rx.tap
            .bind(to: Binder(self) { me, _ in
                me.navigationController?.pushViewController(SearchViewController(rootViewController: SearchContentsViewController()), animated: true)
            }).disposed(by: disposeBag)
    }

    private func setUpTabBar() {
        tabBar.delegate = self
        tabBar.items = [
            UITabBarItem(title: "DAY1", image: nil, tag: 0),
            UITabBarItem(title: "DAY2", image: nil, tag: 1),
            UITabBarItem(title: "EVENT", image: nil, tag: 2),
            UITabBarItem(title: "MYPLAN", image: nil, tag: 3),
        ]
        tabBar.alignment = .justified
        tabBar.itemAppearance = .titles
        tabBar.tintColor = .white

        tabBar.autoresizingMask = [.flexibleWidth, .flexibleBottomMargin]
        tabBar.sizeToFit()
        view.addSubview(tabBar)
        tabBar.translatesAutoresizingMaskIntoConstraints = false
        tabBar.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        tabBar.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        tabBar.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
    }

    private func setUpContainerView() {
        let viewController = SessionPageViewController(filterViewModel: filterViewModel, transitionStyle: .scroll, navigationOrientation: .horizontal)
        viewController.filterViewControllerDelegate = self
        insert(viewController)

        view.addSubview(embeddedView!)

        let panGesture = UIPanGestureRecognizer()
        panGesture.delegate = self

        var baseY: CGFloat = 0
        panGesture.rx.event.asDriver()
            .withLatestFrom(filterViewModel.isFocusedOnEmbeddedView) { ($0, $1) }
            .drive(onNext: { [weak self] item in
                let (recognizer, isFocused) = item
                guard let containerView = self?.embeddedView else { return }

                switch recognizer.state {
                case .began:
                    recognizer.setTranslation(.zero, in: containerView)
                    baseY = containerView.frame.minY
                case .changed:
                    let moved = recognizer.translation(in: containerView)
                    let nextY = containerView.frame.origin.y + moved.y
                    if nextY < baseY && isFocused
                        || nextY > baseY && !isFocused {
                        break
                    }
                    self?.embeddedView.frame.origin.y = baseY + moved.y
                case .ended:
                    let thresholdPercentage: CGFloat = 0.1
                    let moved = recognizer.translation(in: containerView)
                    let movePercentage = moved.y / containerView.frame.height
                    let currentY = containerView.frame.origin.y
                    if (currentY <= baseY && isFocused)
                        || (currentY >= baseY && !isFocused) {
                        break
                    }
                    if abs(movePercentage) > thresholdPercentage {
                        self?.filterViewModel.toggleEmbeddedView()
                    } else {
                        self?.filterViewModel.turnBackEmbeddedView()
                    }
                default:
                    break
                }
            }).disposed(by: disposeBag)
        embeddedView.addGestureRecognizer(panGesture)
    }

    private func setupFilterView() {
        filterView = FilterView()
        view.addSubview(filterView)
        filterView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            filterView.topAnchor.constraint(equalTo: tabBar.bottomAnchor),
            filterView.leftAnchor.constraint(equalTo: view.leftAnchor),
            filterView.rightAnchor.constraint(equalTo: view.rightAnchor),
            filterView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
    }

    private func bindToViewModel() {
        filterViewModel.isFocusedOnEmbeddedView
            .skip(1)
            .drive(Binder(self) { me, isFocusedOnEmbeddedView in
                if isFocusedOnEmbeddedView {
                    UIView.animate(withDuration: 0.2) {
                        me.embeddedView.frame.origin.y = me.view.frame.height - (me.embeddedView?.frame.height)!
                    }
                } else {
                    UIView.animate(withDuration: 0.2) {
                        me.embeddedView.frame.origin.y = me.view.frame.height - 100
                    }
                }
            }).disposed(by: disposeBag)
        filterViewModel.sessionContents
            .drive(filterView.collectionView.rx.items(dataSource: FilterViewDataSource()))
            .disposed(by: disposeBag)

        filterView.collectionView.rx.itemSelected.asObservable()
            .compactMap { [weak self] indexPath -> Any? in
                guard let self = self else { return nil }
                do {
                    return try self.filterView.collectionView.rx.model(at: indexPath)
                } catch {
                    return nil
                }
            }
            .bind(to: Binder(self) { me, chip in
                me.filterViewModel.selectChip(chip: chip)
            }).disposed(by: disposeBag)

        filterView.collectionView.rx.itemDeselected.asObservable()
            .compactMap { [weak self] indexPath -> Any? in
                guard let self = self else { return nil }
                do {
                    return try self.filterView.collectionView.rx.model(at: indexPath)
                } catch {
                    return nil
                }
            }
            .bind(to: Binder(self) { me, chip in
                me.filterViewModel.deselectChip(chip: chip)
            }).disposed(by: disposeBag)

        filterView.headerView.resetButton.rx.tap.asObservable()
            .bind(to: Binder(self) { me, _ in
                me.filterView.collectionView.visibleCells.forEach { cell in
                    guard let indexPath = me.filterView.collectionView.indexPath(for: cell) else {
                        return
                    }
                    me.filterView.collectionView.deselectItem(at: indexPath, animated: false)
                }
                me.filterViewModel.resetSelected()
            }).disposed(by: disposeBag)
    }
}

extension FilterViewController {
    func insert(_ controller: SessionPageViewController) {
        if let controller = embeddedViewController,
            let view = embeddedView {
            controller.willMove(toParent: nil)
            controller.removeFromParent()
            embeddedViewController = nil

            view.removeFromSuperview()
            embeddedView = nil
        }
        controller.willMove(toParent: self)
        addChild(controller)
        embeddedViewController = controller
        embeddedView = controller.view
        embeddedView?.backgroundColor = .white
    }
}

extension FilterViewController: UIGestureRecognizerDelegate {
    func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        if let panGesture = gestureRecognizer as? UIPanGestureRecognizer {
            let translation = panGesture.translation(in: gestureRecognizer.view)
            return abs(translation.x) < abs(translation.y)
        }
        return true
    }
}

/// - MDCTabBarDelegate
extension FilterViewController: MDCTabBarDelegate {
    func tabBar(_ tabBar: MDCTabBar, didSelect item: UITabBarItem) {
        switch item.tag {
        case 0:
            embeddedViewController?.setViewControllers(type: .day1)
        case 1:
            embeddedViewController?.setViewControllers(type: .day2)
        case 2:
            embeddedViewController?.setViewControllers(type: .event)
        case 3:
            embeddedViewController?.setViewControllers(type: .myPlan)
        default:
            break
        }
    }
}

/// - FilterViewControllerDelegate
extension FilterViewController: FilterViewControllerDelegate {
    func shouldChangeTab(index: Int) {
        UIView.animate(withDuration: 0.2) { [weak self] in
            guard let self = self else { return }
            self.tabBar.selectedItem = self.tabBar.items[index]
        }
    }
}
