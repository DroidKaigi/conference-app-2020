//
//  ViewController.swift
//  DroidKaigi 2020
//
//  Created by takahiro menju on 2019/12/10.
//  Copyright © 2019 DroidKaigi. All rights reserved.
//

import UIKit
import api

class ViewController: UIViewController {

    @IBOutlet weak var label: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
    ApiComponentKt.generateDroidKaigiApi().getSessions(callback: { (response) in
            self.label.text = "There are " + String(response.sessions.count) + " sessions!"
        }) { (exception) in
            
        }
    }

}

