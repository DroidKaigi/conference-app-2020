require 'json'

reponse = JSON.parse(File.read(ENV.fetch('DEPLOYGATE_API_RESPONSE_PATH')))

return markdown("DeployGate returned an error. Please check the CI logs.") unless response['error'] == 'false'

distribution_url = response.dig('results', 'distribution', 'url')

return markdown("No distribution was found") if distribution_url.nil?

markdown("Your apk has been deployed to #{distribution_url}. Anyone can try your changes via the link.")
