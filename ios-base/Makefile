init:
	mint run xcodegen
	bundle install --path vendor/bundle
	bundle exec pod install

xcode:
	mint run xcodegen
	bundle exec pod install

test: xcode
	set -o pipefail && \
	xcrun xcodebuild -workspace 'DroidKaigi 2020.xcworkspace' \
	-scheme 'DroidKaigi 2020' \
	-destination 'platform=iOS Simulator,name=iPhone 11' \
	clean test | bundle exec xcpretty
