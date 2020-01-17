//
//  SessionViewController.swift
//  DroidKaigi 2020
//
//  Created by takahiro menju on 2019/12/10.
//  Copyright © 2019 DroidKaigi. All rights reserved.
//

import UIKit
import api
import MaterialComponents

final class SessionViewController: UIViewController {

    var appBarViewController = MDCAppBarViewController()

    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.frame = UIScreen.main.bounds
        self.view.backgroundColor = .white

        let label = UILabel(frame: .init(x: 40, y: 40, width: 100, height: 50))
        label.text = "hoge"
        view.addSubview(label)
    }
}

