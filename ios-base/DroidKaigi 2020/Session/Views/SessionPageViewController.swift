import UIKit

enum SessionViewControllerType: Int {
    case day1 = 1
    case day2 = 2
    case myPlan = 3

    var date: Date? {
        let calendar = Calendar(identifier: .gregorian)
        switch self {
        case .day1:
            return calendar.date(from: .init(year: 2020, month: 2, day: 20))
        case .day2:
            return calendar.date(from: .init(year: 2020, month: 2, day: 20))
        case .myPlan:
            return nil
        }
    }
}

final class SessionPageViewController: UIPageViewController {

    let viewModel: SessionViewModel

    weak var filterViewControllerDelegate: FilterViewControllerDelegate?

    var selectedViewControllerIndex: Int = 0
    var sessionViewControllers: [UIViewController] = []

    init(viewModel: SessionViewModel, transitionStyle style: UIPageViewController.TransitionStyle, navigationOrientation: UIPageViewController.NavigationOrientation, options: [UIPageViewController.OptionsKey : Any]? = nil) {
        self.viewModel = viewModel
        super.init(transitionStyle: style, navigationOrientation: navigationOrientation, options: options)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        sessionViewControllers = [
            SessionViewController(viewModel: viewModel, sessionViewType: .day1),
            SessionViewController(viewModel: viewModel, sessionViewType: .day2),
            SessionViewController(viewModel: viewModel, sessionViewType: .myPlan),
        ]
        setViewControllers([sessionViewControllers[0]], direction: .forward, animated: true)
        selectedViewControllerIndex = 0
        dataSource = self
        delegate = self

        // rx
        viewModel.viewDidLoad()
    }

    func setViewControllers(type: SessionViewControllerType) {
        let direction: UIPageViewController.NavigationDirection =
            selectedViewControllerIndex < type.rawValue
            ? .forward : .reverse
        selectedViewControllerIndex = type.rawValue
        setViewControllers([sessionViewControllers[type.rawValue - 1]], direction: direction, animated: true)
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

extension SessionPageViewController: UIPageViewControllerDelegate {
    func pageViewController(_ pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [UIViewController], transitionCompleted completed: Bool) {
        guard let changedVC = pageViewController.viewControllers?.first,
            let changedIndex = sessionViewControllers.firstIndex(of: changedVC) else {
            return
        }
        filterViewControllerDelegate?.shouldChangeTab(index: changedIndex)
    }
}
