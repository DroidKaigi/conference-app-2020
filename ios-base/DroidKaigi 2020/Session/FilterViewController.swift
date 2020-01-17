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
    }
}
