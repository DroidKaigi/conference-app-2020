//
//  FilterViewController.swift
//  DroidKaigi 2020
//
//  Created by 伊藤凌也 on 2020/01/17.
//

import UIKit
import MaterialComponents

final class FilterViewController: UIViewController {

    var appBar = MDCAppBar()

    var containerView: UIView = {
        //TODO: Change the following line from UIView to ShapedShadowedView and apply the shape.
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

    var isFocusedEmbeddedController: Bool = false {
        didSet {
            UIView.animate(withDuration: 0.2) {
                self.containerView.frame = self.frameForEmbeddedController()
            }
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.backgroundColor = ApplicationScheme.shared.colorScheme.surfaceColor

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

        // AppBar Init
        self.addChild(appBar.headerViewController)
        appBar.addSubviewsToParent()
        MDCAppBarColorThemer.applySemanticColorScheme(ApplicationScheme.shared.colorScheme, to: appBar)
        appBar.navigationBar.translatesAutoresizingMaskIntoConstraints = false

        let tabBar = MDCTabBar()
        tabBar.items = [
            UITabBarItem(title: "DAY1", image: nil, tag: 0),
            UITabBarItem(title: "DAY2", image: nil, tag: 0),
            UITabBarItem(title: "MYPLAN", image: nil, tag: 0),
        ]
        tabBar.alignment = .justified
        tabBar.itemAppearance = .titles

        tabBar.autoresizingMask = [.flexibleWidth, .flexibleBottomMargin]
        tabBar.sizeToFit()
        view.addSubview(tabBar)
        tabBar.translatesAutoresizingMaskIntoConstraints = false
        tabBar.topAnchor.constraint(equalTo: appBar.headerViewController.view.bottomAnchor).isActive = true
        tabBar.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        tabBar.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true

        let viewController = SessionViewController()
        self.insert(viewController)
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        let embeddedFrame = self.frameForEmbeddedController()
        self.containerView.frame = embeddedFrame
        self.embeddedView?.frame = self.containerView.bounds
    }

    func frameForEmbeddedController() -> CGRect {
        var embeddedFrame = self.view.bounds
        var insetHeader = UIEdgeInsets()
        insetHeader.top = self.appBar.headerViewController.view.frame.maxY
        embeddedFrame = embeddedFrame.inset(by: insetHeader)

        if !isFocusedEmbeddedController {
            embeddedFrame.origin.y = self.view.bounds.size.height - self.appBar.navigationBar.frame.height
        }

        if (embeddedView == nil) {
            embeddedFrame.origin.y = self.view.bounds.maxY
        }

        return embeddedFrame
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

            isFocusedEmbeddedController = false
        }
        controller.willMove(toParent: self)
        self.addChild(controller)
        self.embeddedViewController = controller

        self.containerView.addSubview(controller.view)
        self.embeddedView = controller.view
        self.embeddedView?.backgroundColor = .white

        isFocusedEmbeddedController = true
    }
}
