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
