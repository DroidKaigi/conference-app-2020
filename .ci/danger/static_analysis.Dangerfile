# require 'pry'

# Disabled: Report issues to only modified lines except simple messages and markdowns
# github.dismiss_out_of_range_messages(
#   warning: true,
#   error: true,
#   message: false,
#   markdown: false
# )

# Create inline comments to report warning or more serious issues which happen only on modified files
android_lint.tap do |plugin|
  plugin.skip_gradle_task = true
  plugin.filtering = true
  plugin.severity = 'Warning'

  Dir.glob("**/lint-results*.xml").each do |xml|
    plugin.report_file = xml
    plugin.lint(inline_mode: true)
  end
end

# Create a summary report of JUnit format test results
junit.tap do |plugin|
  test_result_dir = ENV.fetch('JUNIT_TEST_RESULT_DIR')

  if File.exist?(test_result_dir)
    plugin.parse_files(Dir.glob("#{test_result_dir}/*.xml"))
    plugin.report
    fail("At least one test case failed") unless junit.failures.empty?
  else
    fail("No test result was found")
  end
end

# Create inline comments to report checkstyle issues which happen only on modified files
checkstyle_reports.tap do |plugin|
  plugin.inline_comment=true

  # Report lint warnings
  Dir.glob("**/checkstyle.xml").each do |xml|
    plugin.report(xml, modified_files_only: true)
  end
end

# If everything is okay, say LGTM to the author
return unless status_report[:errors].empty?

if status_report[:warnings].empty?
  markdown("No issue was reported. Cool!")
else
  markdown("No error was reported but at least one warning was found.")
end
