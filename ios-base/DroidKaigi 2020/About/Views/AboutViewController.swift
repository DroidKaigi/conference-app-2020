import RxCocoa
import RxSwift
import SafariServices
import UIKit

final class AboutViewController: ContentTableViewController {
    private enum CellType {
        case description, access, staff, policy, license, version
    }

    private let disposeBag = DisposeBag()
    private let cells: [CellType] = [.description, .access, .staff, .policy, .license, .version]

    static func instantiate() -> AboutViewController {
        guard let viewController = UIStoryboard(name: "AboutViewController", bundle: .main).instantiateInitialViewController() as? AboutViewController else { fatalError() }
        return viewController
    }

    init() {
        super.init(nibName: nil, bundle: nil)
        title = L10n.aboutDroidKaigi
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        title = L10n.aboutDroidKaigi
    }

    override func viewDidLoad() {
        super.viewDidLoad()
    }
}

extension AboutViewController {
    override func numberOfSections(in tableView: UITableView) -> Int {
        1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        cells.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch cells[indexPath.row] {
        case .description:
            return dequeueReusableCell(withIdentifier: AboutCell.Identifier.description, for: indexPath) { cell in
                cell.titleLabel.text = L10n.whatIsDroidKaigi
                let detail = L10n.droidKaigiDescription
                let paragraphStyle = NSMutableParagraphStyle()
                paragraphStyle.lineHeightMultiple = 1.1
                let attributes: [NSAttributedString.Key: Any] = [.kern: 0.5, .paragraphStyle: paragraphStyle]
                cell.detailLabel?.attributedText = NSMutableAttributedString(string: detail,
                                                                             attributes: attributes)
                cell.onButtonTapped = { button in
                    let urlString: String
                    switch button {
                    case .twitter:
                        urlString = "https://twitter.com/DroidKaigi"
                    case .youtube:
                        urlString = "https://www.youtube.com/channel/UCgK6L-PKx2OZBuhrQ6mmQZw"
                    case .medium:
                        urlString = "https://medium.com/droidkaigi"
                    }
                    guard let url = URL(string: urlString) else { return }
                    UIApplication.shared.open(url)
                }
            }
        case .access:
            return dequeueReusableCell(withIdentifier: AboutCell.Identifier.icon, for: indexPath) { cell in
                cell.titleLabel.text = L10n.access
            }
        case .staff:
            return dequeueReusableCell(withIdentifier: AboutCell.Identifier.basic, for: indexPath) { cell in
                cell.titleLabel.text = L10n.staffs
            }
        case .policy:
            return dequeueReusableCell(withIdentifier: AboutCell.Identifier.basic, for: indexPath) { cell in
                cell.titleLabel.text = L10n.privacyPolicy
            }
        case .license:
            return dequeueReusableCell(withIdentifier: AboutCell.Identifier.basic, for: indexPath) { cell in
                cell.titleLabel.text = L10n.licence
            }
        case .version:
            return dequeueReusableCell(withIdentifier: AboutCell.Identifier.detail, for: indexPath) { cell in
                cell.titleLabel.text = L10n.version
                cell.detailLabel?.text =
                    Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? ""
            }
        }
    }

    private func dequeueReusableCell(withIdentifier: String, for indexPaht: IndexPath, configuration: (AboutCell) -> Void) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: withIdentifier, for: indexPaht)
        guard let aboutCell = cell as? AboutCell else { return cell }
        configuration(aboutCell)
        return aboutCell
    }
}

extension AboutViewController {
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        switch cells[indexPath.row] {
        case .access:
            tableView.deselectRow(at: indexPath, animated: true)
            let address = L10n.tocBuilding
            let latitude = "35.621925"
            let longitude = "139.719063"
            guard canOpenWithGoogleMaps else {
                openWithMaps(address: address, latitude: latitude, longitude: longitude)
                return
            }

            let alertController = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
            let openWithMapsAction = UIAlertAction(title: L10n.maps, style: .default) { [weak self] _ in
                self?.openWithMaps(address: address, latitude: latitude, longitude: longitude)
            }
            let openWithGoogleMapsAction = UIAlertAction(title: L10n.googleMaps, style: .default) { [weak self] _ in
                self?.openWithGoogleMaps(address: address, latitude: latitude, longitude: longitude)
            }
            let cancelAction = UIAlertAction(title: L10n.cancel, style: .cancel)
            alertController.addAction(openWithMapsAction)
            alertController.addAction(openWithGoogleMapsAction)
            alertController.addAction(cancelAction)
            present(alertController, animated: true)
        case .staff:
            break
        case .policy:
            guard let url = URL(string: L10n.privacyPolicyURL) else {
                tableView.deselectRow(at: indexPath, animated: true)
                return
            }
            presentSafariViewController(with: url)
        case .license:
            tableView.deselectRow(at: indexPath, animated: true)
            guard let settingsUrl = URL(string: UIApplication.openSettingsURLString) else {
                return
            }
            if UIApplication.shared.canOpenURL(settingsUrl) {
                UIApplication.shared.open(settingsUrl)
            }
        default:
            break
        }
    }

    private var canOpenWithGoogleMaps: Bool {
        if let url = URL(string: "comgooglemaps://"),
            UIApplication.shared.canOpenURL(url) {
            return true
        } else {
            return false
        }
    }

    private func openWithMaps(address: String, latitude: String, longitude: String) {
        var urlComponents = URLComponents(string: "http://maps.apple.com/")
        urlComponents?.queryItems = [
            URLQueryItem(name: "address", value: address),
            URLQueryItem(name: "ll", value: "\(latitude),\(longitude)"),
        ]
        if let url = urlComponents?.url {
            UIApplication.shared.open(url)
        }
    }

    private func openWithGoogleMaps(address: String, latitude: String, longitude: String) {
        var urlComponents = URLComponents(string: "comgooglemaps://")
        urlComponents?.queryItems = [
            URLQueryItem(name: "q", value: address),
            URLQueryItem(name: "center", value: "\(latitude),\(longitude)"),
        ]
        if let url = urlComponents?.url {
            UIApplication.shared.open(url)
        }
    }

    private func presentSafariViewController(with url: URL) {
        let safariViewController = SFSafariViewController(url: url)
        present(safariViewController, animated: true)
    }
}
