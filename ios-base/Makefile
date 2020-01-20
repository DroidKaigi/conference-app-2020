init:
	xcodegen
	bundle install --path vendor/bundle
	bundle exec pod install
	echo "include ':ios-combined'" > ../ios-settings.gradle
	./build_kotlin_modules.sh
