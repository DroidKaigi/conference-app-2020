//
//  SessionPageViewController.swift
//  DroidKaigi 2020
//
//  Created by 伊藤凌也 on 2020/01/19.
//

import UIKit

enum SessionViewControllerType: Int {
    case day1
    case day2
    case myPlan
}

final class SessionPageViewController: UIPageViewController {

    var selectedViewControllerIndex: Int = 0
    var sessionViewControllers: [UIViewController] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        let viewModel = SessionViewModel()
        sessionViewControllers = [
            SessionViewController(viewModel: viewModel),
            SessionViewController(viewModel: viewModel),
            SessionViewController(viewModel: viewModel),
        ]
        setViewControllers([sessionViewControllers[0]], direction: .forward, animated: true)
        selectedViewControllerIndex = 0
        dataSource = self
    }

    func setViewControllers(type: SessionViewControllerType) {
        let direction: UIPageViewController.NavigationDirection =
            selectedViewControllerIndex < type.rawValue
            ? .forward : .reverse
        selectedViewControllerIndex = type.rawValue
        setViewControllers([sessionViewControllers[type.rawValue]], direction: direction, animated: true)
    }
}

extension SessionPageViewController: UIPageViewControllerDataSource {
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerBefore viewController: UIViewController) -> UIViewController? {
        switch viewController {
        case sessionViewControllers[0]:
            return nil
        case sessionViewControllers[1]:
            selectedViewControllerIndex = 0
            return sessionViewControllers[0]
        case sessionViewControllers[2]:
            selectedViewControllerIndex = 1
            return sessionViewControllers[1]
        default:
            return nil
        }
    }
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerAfter viewController: UIViewController) -> UIViewController? {
        switch viewController {
        case sessionViewControllers[0]:
            selectedViewControllerIndex = 1
            return sessionViewControllers[1]
        case sessionViewControllers[1]:
            selectedViewControllerIndex = 2
            return sessionViewControllers[2]
        case sessionViewControllers[2]:
            return nil
        default:
            return nil
        }
    }
}
