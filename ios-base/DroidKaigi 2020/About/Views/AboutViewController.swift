import ios_combined
import UIKit

class AboutViewController: UITableViewController {
    private enum CellType {
        case description, access, staff, policy, license, version
    }

    private let cells: [CellType] = [.description, .access, .staff, .policy, .license, .version]

    static func instantiate() -> AboutViewController {
        guard let viewController = UIStoryboard(name: "AboutViewController", bundle: .main).instantiateInitialViewController() as? AboutViewController else { fatalError() }
        return viewController
    }

    override func viewDidLoad() {
        title = "DroidKaigiとは"
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
                cell.titleLabel.text = "What is DroidKaigi?"
                let detail = """
                DroidKaigiはエンジニアが主役のAndroidカンファレンスです。
                Android技術情報の共有とコミュニケーションを目的に、2020年2月20日(木)、21日(金)の2日間開催します。
                """
                let paragraphStyle = NSMutableParagraphStyle()
                paragraphStyle.lineHeightMultiple = 1.1
                let attributes: [NSAttributedString.Key: Any] = [.kern: 0.5, .paragraphStyle: paragraphStyle]
                cell.detailLabel?.attributedText = NSMutableAttributedString(string: detail,
                                                                             attributes: attributes)
            }
        case .access:
            return dequeueReusableCell(withIdentifier: AboutCell.Identifier.icon, for: indexPath) { cell in
                cell.titleLabel.text = "会場アクセス"
            }
        case .staff:
            return dequeueReusableCell(withIdentifier: AboutCell.Identifier.basic, for: indexPath) { cell in
                cell.titleLabel.text = "スタッフリスト"
            }
        case .policy:
            return dequeueReusableCell(withIdentifier: AboutCell.Identifier.basic, for: indexPath) { cell in
                cell.titleLabel.text = "プライバシーポリシー"
            }
        case .license:
            return dequeueReusableCell(withIdentifier: AboutCell.Identifier.basic, for: indexPath) { cell in
                cell.titleLabel.text = "ライセンス"
            }
        case .version:
            return dequeueReusableCell(withIdentifier: AboutCell.Identifier.detail, for: indexPath) { cell in
                cell.titleLabel.text = "アプリバージョン"
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
            let address = "TOCビル"
            let latitude = "35.621925"
            let longitude = "139.719063"
            guard canOpenWithGoogleMaps else {
                openWithMaps(address: address, latitude: latitude, longitude: longitude)
                return
            }

            let alertController = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
            let openWithMapsAction = UIAlertAction(title: "マップ", style: .default) { [weak self] _ in
                self?.openWithMaps(address: address, latitude: latitude, longitude: longitude)
            }
            let openWithGoogleMapsAction = UIAlertAction(title: "Google Maps", style: .default) { [weak self] _ in
                self?.openWithGoogleMaps(address: address, latitude: latitude, longitude: longitude)
            }
            let cancelAction = UIAlertAction(title: "キャンセル", style: .cancel)
            alertController.addAction(openWithMapsAction)
            alertController.addAction(openWithGoogleMapsAction)
            alertController.addAction(cancelAction)
            present(alertController, animated: true)
        case .staff:
            break
        case .policy:
            break
        case .license:
            break
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
}
