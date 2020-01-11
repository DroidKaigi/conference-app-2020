# require 'pry'

apkstats.tap do |plugin|
    new_apk_filepath = ENV.fetch('NEW_APK_FILEPATH')
    current_latest_apk_filepath = ENV.fetch('LATEST_APK_FILEPATH')

    unless File.exist?(new_apk_filepath)
        warn("#{new_apk_filepath} was not found.")
        break
    end

    unless File.exist?(current_latest_apk_filepath)
        warn("#{current_latest_apk_filepath} was not found.")
        break
    end

    plugin.apk_filepath = new_apk_filepath
    plugin.compare_with(current_latest_apk_filepath, do_report: true)
end
