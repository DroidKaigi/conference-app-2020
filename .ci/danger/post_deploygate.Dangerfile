# require 'pry'

require 'json'

distribution_url = ENV['DEPLOYGATE_DISTRIBUTION_URL']

return markdown("No distribution was found") if distribution_url.nil?

markdown("Your apk has been deployed to #{distribution_url}. Anyone can try your changes via the link.")
