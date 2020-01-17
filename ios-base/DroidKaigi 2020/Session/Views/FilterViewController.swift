//
//  FilterViewController.swift
//  DroidKaigi 2020
//
//  Created by 伊藤凌也 on 2020/01/17.
//

import UIKit
import RxSwift
import RxCocoa
import MaterialComponents

final class FilterViewController: UIViewController {

    private let disposeBag = DisposeBag()

    let appBar = MDCAppBar()
    let tabBar = MDCTabBar()

    var containerView: UIView = {
        let view = ShapedShadowedView(frame: .zero)
        let shapeGenerator = MDCRectangleShapeGenerator()
        shapeGenerator.topLeftCorner =
            ApplicationScheme.shared.shapeScheme.largeComponentShape.topLeftCorner
        view.shapeGenerator = shapeGenerator
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = ApplicationScheme.shared.colorScheme.surfaceColor
        return view
    }()

    var embeddedView: UIView?
    var embeddedViewController: UIViewController?

    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.backgroundColor = ApplicationScheme.shared.colorScheme.surfaceColor
        setUpAppBar()
        setUpTabBar()
        setUpContainerView()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        let embeddedFrame = self.frameForEmbeddedController()
        self.containerView.frame = embeddedFrame
        self.embeddedView?.frame = self.containerView.bounds
    }

    private func frameForEmbeddedController() -> CGRect {
        var embeddedFrame = self.view.bounds
        var insetHeader = UIEdgeInsets()
        let bottomMargin: CGFloat = 24
        insetHeader.top = self.appBar.headerViewController.view.frame.maxY
            + tabBar.bounds.maxY + bottomMargin
        embeddedFrame = embeddedFrame.inset(by: insetHeader)

        if (embeddedView == nil) {
            embeddedFrame.origin.y = self.view.bounds.maxY
        }

        return embeddedFrame
    }

    private func setUpAppBar() {
        let menuImage = UIImage(named: "ic_menu")
        let templateMenuImage = menuImage?.withRenderingMode(.alwaysTemplate)
        let menuItem = UIBarButtonItem(image: templateMenuImage,
                                       style: .plain,
                                       target: self,
                                       action: nil)
        let logoImage = UIImage(named: "logo")
        let templateLogoImage = logoImage?.withRenderingMode(.alwaysOriginal)
        let logoItem = UIBarButtonItem(image: templateLogoImage, style: .plain, target: nil, action: nil)
        let searchImage = UIImage(named: "ic_search")
        let templateSearchImage = searchImage?.withRenderingMode(.alwaysTemplate)
        let searchItem = UIBarButtonItem(image: templateSearchImage,
                                         style: .plain,
                                         target: self,
                                         action: nil)
        self.navigationItem.leftBarButtonItems = [menuItem, logoItem]
        self.navigationItem.rightBarButtonItems = [searchItem]

        self.addChild(appBar.headerViewController)
        appBar.addSubviewsToParent()
        MDCAppBarColorThemer.applySemanticColorScheme(ApplicationScheme.shared.colorScheme, to: appBar)
        appBar.navigationBar.translatesAutoresizingMaskIntoConstraints = false
    }

    private func setUpTabBar() {
        tabBar.items = [
            UITabBarItem(title: "DAY1", image: nil, tag: 0),
            UITabBarItem(title: "DAY2", image: nil, tag: 0),
            UITabBarItem(title: "MYPLAN", image: nil, tag: 0),
        ]
        tabBar.alignment = .justified
        tabBar.itemAppearance = .titles
        tabBar.tintColor = .white

        tabBar.autoresizingMask = [.flexibleWidth, .flexibleBottomMargin]
        tabBar.sizeToFit()
        view.addSubview(tabBar)
        tabBar.translatesAutoresizingMaskIntoConstraints = false
        tabBar.topAnchor.constraint(equalTo: appBar.headerViewController.view.bottomAnchor).isActive = true
        tabBar.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        tabBar.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
    }

    private func setUpContainerView() {
        self.view.addSubview(containerView)

        let viewController = SessionViewController()
        self.insert(viewController)

        let animator = UIViewPropertyAnimator(duration: 0.8, curve: .easeInOut) { [weak self] in
            guard let self = self else { return }
            self.containerView.frame.origin.y += self.containerView.frame.height - 100
        }
        animator.pausesOnCompletion = true

        let panGesture = UIPanGestureRecognizer()
        panGesture.rx.event.asDriver()
            .drive(onNext: { [weak self] recognizer in
                guard let containerView = self?.containerView else { return }

                switch recognizer.state {
                case .began:
                    animator.isReversed = false
                    animator.isReversed = animator.fractionComplete > 0.5
                    recognizer.setTranslation(.zero, in: containerView)
                case .changed:
                    let moved = recognizer.translation(in: containerView)
                    let movePercentage = abs(moved.y / containerView.frame.height)
                    animator.fractionComplete = movePercentage
                case .ended:
                    let thresholdPercentage: CGFloat = 0.1
                    if animator.fractionComplete < thresholdPercentage {
                        animator.isReversed.toggle()
                    }
                    animator.startAnimation()
                default:
                    break
                }
            }).disposed(by: disposeBag)
        containerView.addGestureRecognizer(panGesture)
    }
}

extension FilterViewController {
    func insert(_ controller: UIViewController) {
        if let controller = self.embeddedViewController,
            let view = self.embeddedView {
            controller.willMove(toParent: nil)
            controller.removeFromParent()
            self.embeddedViewController = nil

            view.removeFromSuperview()
            self.embeddedView = nil
        }
        controller.willMove(toParent: self)
        self.addChild(controller)
        self.embeddedViewController = controller

        self.containerView.addSubview(controller.view)
        self.embeddedView = controller.view
        self.embeddedView?.backgroundColor = .white
    }
}
