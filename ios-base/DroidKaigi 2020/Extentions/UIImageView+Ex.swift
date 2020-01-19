//
//  UIImageView+Ex.swift
//  DroidKaigi 2020
//
//  Created by 伊藤凌也 on 2020/01/18.
//

import UIKit

extension UIImageView {
    func loadImage(url: URL?) {
        guard let url = url else {
            return
        }
        DispatchQueue.global().async { [weak self] in
            do {
                let data = try Data(contentsOf: url)
                let image = UIImage(data: data)
                DispatchQueue.main.async {
                    self?.image = image
                }
            } catch let e {
                print(e.localizedDescription)
            }
        }
    }
}
