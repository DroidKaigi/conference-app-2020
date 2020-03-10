import dependencies.Dep
import dependencies.Packages
import dependencies.Versions

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.firebase.crashlytics")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.releaseshub.gradle.plugin")
}

android {
    compileSdkVersion(Versions.androidCompileSdkVersion)
    defaultConfig {
        applicationId = Packages.name
        minSdkVersion(Versions.androidMinSdkVersion)
        targetSdkVersion(Versions.androidTargetSdkVersion)
        versionCode = Versions.androidVersionCode
        versionName = Versions.androidVersionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments = mapOf("clearPackageData" to "true")
    }
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release").initWith(getByName("debug"))

        val releaseKeystore = file("release.keystore")

        if (rootProject.ext.get("isReleaseBuild") as Boolean) {
            assert(releaseKeystore.exists())
            assert(System.getenv("RELEASE_KEYSTORE_STORE_PASSWORD") != null)
            assert(System.getenv("RELEASE_KEYSTORE_KEY_PASSWORD") != null)
        }

        if (releaseKeystore.exists()) {
            getByName("release") {
                storeFile = releaseKeystore
                storePassword = System.getenv("RELEASE_KEYSTORE_STORE_PASSWORD")
                keyAlias = "droidkaigi"
                keyPassword = System.getenv("RELEASE_KEYSTORE_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = Packages.debugNameSuffix

            // When using useFakeSplit, you need to push split to /sdcard/Android/data/io.github.droidkaigi.confsched2020.debug/files.
            // Details can be found in `script/install_fakesplit`.
            buildConfigField(
                "boolean",
                "USE_FAKE_SPLIT",
                "Boolean.parseBoolean(\"${project.hasProperty("useFakeSplit")}\")"
            )
        }
        getByName("release") {
            buildConfigField("boolean", "USE_FAKE_SPLIT", "false")
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    dataBinding {
        isEnabled = true
    }
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/*.version")
        exclude("META-INF/proguard/*.pro")
    }
    dynamicFeatures = hashSetOf(
            ":feature:staff",
            ":feature:preference",
            ":feature:contributor"
    )
    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        animationsDisabled = true
    }
}

dependencies {
    implementation(project(":feature:session"))
    implementation(project(":feature:system"))
    implementation(project(":feature:sponsor"))
    implementation(project(":feature:announcement"))
    implementation(project(":feature:about"))
    implementation(project(":feature:floormap"))
    implementation(project(":feature:session_survey"))

    implementation(project(":data:repository"))
    implementation(project(":data:db"))
    implementation(project(":data:firestore"))
    implementation(project(":data:api"))
    implementation(project(":data:device"))

    implementation(project(":corecomponent:androidcomponent"))
    implementation(project(":ext:log"))

    implementation(Dep.Kotlin.stdlibJvm)
    implementation(Dep.AndroidX.appCompat)
    implementation(Dep.AndroidX.coreKtx)
    implementation(Dep.AndroidX.constraint)
    implementation(Dep.AndroidX.activityKtx)
    implementation(Dep.AndroidX.Work.runtimeKtx)
    implementation(Dep.Firebase.firestoreKtx)
    implementation(Dep.Firebase.crashlytics)
    implementation(Dep.Firebase.analytics)
    implementation(Dep.AndroidX.emoji)

    implementation(Dep.Dagger.core)
    implementation(Dep.Dagger.androidSupport)
    implementation(Dep.Dagger.android)
    kapt(Dep.Dagger.compiler)
    kapt(Dep.Dagger.androidProcessor)
    compileOnly(Dep.Dagger.assistedInjectAnnotations)
    kapt(Dep.Dagger.assistedInjectProcessor)
    implementation(Dep.Groupie.groupie)

    testImplementation(Dep.Test.junit)
    androidTestImplementation(Dep.Test.testRunner)
    androidTestImplementation(Dep.Test.testRules)
    androidTestImplementation(Dep.Test.espressoCore)
    androidTestImplementation(Dep.Test.testCoreKtx)
    androidTestImplementation(Dep.Test.androidJunit4Ktx)
    androidTestUtil(Dep.Test.orchestrator)

    Dep.Hyperion.hyperionPlugins.forEach {
        debugImplementation(it)
    }
    debugImplementation(Dep.Stetho.stetho)
    debugImplementation(Dep.LeakCanary.leakCanary)
    kapt(Dep.Google.autoservice)
}
releasesHub {
    dependenciesBasePath = "buildSrc/src/main/java/dependencies/"
    dependenciesClassNames = listOf("Dep.kt")
    pullRequestEnabled = true
    gitHubRepositoryOwner = "droidkaigi"
    gitHubRepositoryName = "conference-app-2020"
    pullRequestsMax = 2
    gitHubUserName = "takahirom"
    gitHubUserEmail = "takam.dev@gmail.com"
}

apply(mapOf("plugin" to "com.google.gms.google-services"))
