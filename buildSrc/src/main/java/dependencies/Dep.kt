package dependencies

@Suppress("unused", "MayBeConstant")
object Dep {
    object GradlePlugin {
        val android = "com.android.tools.build:gradle:3.6.0-rc03"
        val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61"
        val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:1.3.61"
        val playServices = "com.google.gms:google-services:4.3.3"
        val safeArgs =
            "androidx.navigation:navigation-safe-args-gradle-plugin:2.3.0-alpha03"
        val jetifier = "com.android.tools.build.jetifier:jetifier-processor:1.0.0-beta05"
        val licensesPlugin = "com.google.android.gms:oss-licenses-plugin:0.10.2"
        val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:2.0.0-beta01"
        val iconRibbonPlugin = "com.akaita.android:easylauncher:1.3.1"
        val gradleVersionsPlugin = "com.github.ben-manes:gradle-versions-plugin:0.22.0"
        val releaseHub = "com.releaseshub:releases-hub-gradle-plugin:1.3.1"
    }

    object Test {
        val junit = "junit:junit:4.12"
        val testRunner = "androidx.test:runner:1.3.0-alpha02"
        val testRules = "androidx.test:rules:1.3.0-alpha02"
        val testCoreKtx = "androidx.test:core-ktx:1.2.1-alpha02"
        val androidJunit4Ktx = "androidx.test.ext:junit-ktx:1.1.2-alpha02"
        val orchestrator = "androidx.test:orchestrator:1.3.0-alpha02"
        val archCore = "androidx.arch.core:core-testing:2.1.0"
        val liveDataTestingKtx = "com.jraska.livedata:testing-ktx:1.1.0"
        val espressoCore = "androidx.test.espresso:espresso-core:3.3.0-alpha02"
        val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.3"
        val kotlinTestAssertions = "io.kotlintest:kotlintest-assertions:3.1.10"
        val testingKtx =
            "androidx.navigation:navigation-testing-ktx:2.3.0-alpha03"

        object KotlinMultiPlatform {
            val jvmModuleTest = "org.jetbrains.kotlin:kotlin-test"
            val jvmModuleTestJunit = "org.jetbrains.kotlin:kotlin-test-junit"
            val commonModuleTest = "org.jetbrains.kotlin:kotlin-test-common"
            val commonModuleTestAnnotations = "org.jetbrains.kotlin:kotlin-test-annotations-common"
        }

        val slf4j = "org.slf4j:slf4j-simple:1.7.25"
    }

    object AndroidX {
        val jetifier = "com.android.tools.build.jetifier:jetifier-core:1.0.0-beta02"
        val appCompat = "androidx.appcompat:appcompat:1.1.0"
        val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
        val constraint = "androidx.constraintlayout:constraintlayout:2.0.0"
        val emoji = "androidx.emoji:emoji-appcompat:1.0.0"
        val design = "com.google.android.material:material:1.2.0-alpha05"
        val coreKtx = "androidx.core:core-ktx:1.2.0"
        val preference = "androidx.preference:preference:1.1.0"
        val browser = "androidx.browser:browser:1.2.0"
        val activityKtx = "androidx.activity:activity-ktx:1.1.0"
        val fragmentKtx = "androidx.fragment:fragment-ktx:1.2.2"

        val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata:2.2.0"
        val liveDataCoreKtx = "androidx.lifecycle:lifecycle-livedata-core-ktx:2.2.0"
        val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0"

        object Room {
            val compiler = "androidx.room:room-compiler:2.2.4"
            val runtime = "androidx.room:room-runtime:2.2.4"
            val coroutine = "androidx.room:room-ktx:2.2.4"
        }

        object Navigation {
            val runtimeKtx = "androidx.navigation:navigation-runtime-ktx:2.3.0-alpha03"
            val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:2.3.0-alpha03"
            val uiKtx = "androidx.navigation:navigation-ui-ktx:2.3.0-alpha03"
            val dynamicFeaturesFragment = "androidx.navigation:navigation-dynamic-features-fragment:2.3.0-alpha03"
        }

        object Work {
            val runtimeKtx = "androidx.work:work-runtime-ktx:2.3.1"
        }
    }

    object Kotlin {
        val stdlibCommon = "org.jetbrains.kotlin:kotlin-stdlib-common:1.3.61"
        val stdlibJvm = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61"
        val coroutinesCommon =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.3"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3"
        val coroutinesNative =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.3.3"
        val coroutinesIosX64 =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-iosx64:1.3.3"
        val coroutinesIosArm64 =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-iosarm64:1.3.3"
        val androidCoroutinesDispatcher =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.3"
        val coroutinesReactive =
            "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.3.3"
        val coroutinesPlayServices =
            "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.3"
        val serializationCommon =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.14.0"
        val serializationAndroid =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0"
        val serializationNative =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:0.14.0"
        val serializationIosX64 =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime-iosx64:0.14.0"
        val serializationIosArm64 =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime-iosarm64:0.14.0"
    }

    object Firebase {
        val core = "com.google.firebase:firebase-core:16.0.4"
        val firestoreKtx = "com.google.firebase:firebase-firestore-ktx:20.2.0"
        val auth = "com.google.firebase:firebase-auth:18.1.0"
        val crashlytics = "com.google.firebase:firebase-crashlytics:17.0.0-beta01"
        val analytics = "com.google.firebase:firebase-analytics:17.2.2"
        val messaging = "com.google.firebase:firebase-messaging:20.1.0"
    }

    object PlayServices {
        val auth = "com.google.android.gms:play-services-auth:16.0.1"
        val licensesPlugin = "com.google.android.gms:play-services-oss-licenses:17.0.0"
    }

    object Play {
        val core = "com.google.android.play:core:1.6.4"
    }

    object Dagger {
        val core = "com.google.dagger:dagger:2.26"
        val compiler = "com.google.dagger:dagger-compiler:2.26"
        val androidSupport = "com.google.dagger:dagger-android-support:2.26"
        val android = "com.google.dagger:dagger-android:2.26"
        val androidProcessor = "com.google.dagger:dagger-android-processor:2.26"
        val assistedInjectAnnotations =
            "com.squareup.inject:assisted-inject-annotations-dagger2:0.5.2"
        val assistedInjectProcessor = "com.squareup.inject:assisted-inject-processor-dagger2:0.5.2"
    }

    object Ktor {
        val clientCommon = "io.ktor:ktor-client-core:1.3.1"
        val clientAndroid = "io.ktor:ktor-client-okhttp:1.3.1"
        val clientIos = "io.ktor:ktor-client-ios:1.3.1"
        val clientIosArm64 = "io.ktor:ktor-client-ios-iosarm64:1.3.1"
        val clientIosX64 = "io.ktor:ktor-client-ios-iosx64:1.3.1"
        val jsonCommon = "io.ktor:ktor-client-json:1.3.1"
        val jsonJvm = "io.ktor:ktor-client-json-jvm:1.3.1"
        val jsonNative = "io.ktor:ktor-client-json-native:1.3.1"
        val jsonIosArm64 = "io.ktor:ktor-client-json-iosarm64:1.3.1"
        val jsonIosIosX64 = "io.ktor:ktor-client-json-iosx64:1.3.1"
        val serializationCommon = "io.ktor:ktor-client-serialization:1.3.1"
        val serializationJvm = "io.ktor:ktor-client-serialization-jvm:1.3.1"
        val serializationNative = "io.ktor:ktor-client-serialization-native:1.3.1"
        val serializationIosArm64 = "io.ktor:ktor-client-serialization-iosarm64:1.3.1"
        val serializationIosX64 = "io.ktor:ktor-client-serialization-iosx64:1.3.1"
    }

    object OkHttp {
        val client = "com.squareup.okhttp3:okhttp:4.0.1"
        val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.0.1"
        val okio = "com.squareup.okio:okio:1.14.0"
    }

    val liveEvent = "com.github.hadilq.liveevent:liveevent:1.0.1"

    object LeakCanary {
        val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.1"
    }

    object Stetho {
        val stetho = "com.facebook.stetho:stetho:1.5.1"
    }

    object Hyperion {
        val hyperionPlugins = listOf(
            "com.willowtreeapps.hyperion:hyperion-core:0.9.27",
            "com.willowtreeapps.hyperion:hyperion-attr:0.9.27",
            "com.willowtreeapps.hyperion:hyperion-measurement:0.9.27",
            "com.willowtreeapps.hyperion:hyperion-disk:0.9.27",
            "com.willowtreeapps.hyperion:hyperion-recorder:0.9.27",
            "com.willowtreeapps.hyperion:hyperion-phoenix:0.9.27",
            "com.willowtreeapps.hyperion:hyperion-crash:0.9.27",
            "com.willowtreeapps.hyperion:hyperion-shared-preferences:0.9.27",
            "com.willowtreeapps.hyperion:hyperion-geiger-counter:0.9.27",
            "com.willowtreeapps.hyperion:hyperion-build-config:0.9.27",
            "com.willowtreeapps.hyperion:hyperion-plugin:0.9.27"
        )
    }

    object Groupie {
        val groupie = "com.xwray:groupie:2.7.2"
        val databinding = "com.xwray:groupie-databinding:2.7.2"
    }

    object Coil {
        val coil = "io.coil-kt:coil:0.9.5"
    }

    object Klock {
        val common = "com.soywiz.korlibs.klock:klock:1.8.6"
    }

    object MockK {
        val jvm = "io.mockk:mockk:1.9.3"
        val common = "io.mockk:mockk-common:1.9.3"
    }

    object Timber {
        val common = "com.jakewharton.timber:timber-common:5.0.0-SNAPSHOT"
        val jdk = "com.jakewharton.timber:timber-jdk:5.0.0-SNAPSHOT"
        val android = "com.jakewharton.timber:timber-android:5.0.0-SNAPSHOT"
    }

    object Insetter {
        val insetter = "dev.chrisbanes:insetter-ktx:0.2.0"
    }

    object PhotoView {
        val photoview =  "com.github.chrisbanes:PhotoView:2.3.0"
    }

    object Google {
        val autoservice = "com.google.auto.service:auto-service:1.0-rc6"
    }

    object Store {
        val store = "com.dropbox.mobile.store:store4:4.0.0-alpha01"
    }
}
