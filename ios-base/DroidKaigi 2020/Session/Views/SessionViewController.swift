//
//  SessionViewController.swift
//  DroidKaigi 2020
//
//  Created by takahiro menju on 2019/12/10.
//  Copyright © 2019 DroidKaigi. All rights reserved.
//

import UIKit
import MaterialComponents

final class SessionViewController: UIViewController {

    @IBOutlet weak var filteredSessionCountLabel: UILabel! {
        didSet {
            filteredSessionCountLabel.font = ApplicationScheme.shared.typographyScheme.caption
        }
    }
    @IBOutlet weak var filterButton: MDCButton! {
        didSet {
            filterButton.applyTextTheme(withScheme: ApplicationScheme.shared.buttonScheme)
            let filterListImage = UIImage(named: "ic_filter_list")
            let templateFilterListImage = filterListImage?.withRenderingMode(.alwaysTemplate)
            filterButton.setImage(templateFilterListImage, for: .normal)
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
    }
}

