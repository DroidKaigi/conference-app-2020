// TODO: replace build.gradle in root
//rootProject.buildFileName = "build.gradle.kts"
include(":android-base",
        ":feature:preference",
        ":feature:session",
        ":feature:staff",
        ":feature:contributor",
        ":feature:sponsor",
        ":feature:system",
        ":feature:announcement",
        ":feature:about",
        ":feature:floormap",
        ":feature:session_survey",
        ":corecomponent:androidcomponent",
        ":corecomponent:androidtestcomponent",
        ":model",
        ":data:api",
        ":data:db",
        ":data:device",
        ":data:firestore",
        ":data:repository",
        ":ext:log"
)

enableFeaturePreview("GRADLE_METADATA")

rootProject.name="conference-app-2020"

if (System.getenv("BUILD_IOS") == "true") {
  include(":ioscombined")
}
