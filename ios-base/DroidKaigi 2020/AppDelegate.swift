import Material
import Nuke
import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        if #available(iOS 13, *) {
        } else {
            let window = UIWindow(frame: UIScreen.main.bounds)
            UINavigationBar.appearance().isTranslucent = false

            let backButtonBackgroundImage = #imageLiteral(resourceName: "ic_back")
            UINavigationBar.appearance().backIndicatorImage = backButtonBackgroundImage
            UINavigationBar.appearance().backIndicatorTransitionMaskImage = backButtonBackgroundImage

            UINavigationBar.appearance().titleTextAttributes = [.foregroundColor: UIColor.white]

            let vc = FilterViewController()
            let nvc = NavigationController(rootViewController: vc)
            let root = NavigationDrawerController(rootViewController: nvc, leftViewController: SidebarViewController.instantiate(rootViewController: nvc))
            window.rootViewController = root
            self.window = window
            self.window?.makeKeyAndVisible()

            // Configure cache
            ImageCache.shared.costLimit = 1024 * 1024 * 50 // 50 MB
            ImageCache.shared.countLimit = 100
            ImageCache.shared.ttl = 120 // Invalidate image after 120 sec
        }

        return true
    }

    // MARK: UISceneSession Lifecycle

    @available(iOS 13.0, *)
    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    @available(iOS 13.0, *)
    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }
}
