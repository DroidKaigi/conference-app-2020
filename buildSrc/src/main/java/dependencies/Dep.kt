package dependencies

@Suppress("unused")
object Dep {
    object GradlePlugin {
        val android = "com.android.tools.build:gradle:3.6.0-rc01"
        val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
        val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:${Kotlin.version}"
        val playServices = "com.google.gms:google-services:4.3.3"
        val safeArgs =
            "androidx.navigation:navigation-safe-args-gradle-plugin:${AndroidX.Navigation.version}"
        val jetifier = "com.android.tools.build.jetifier:jetifier-processor:1.0.0-beta05"
        val licensesPlugin = "com.google.android.gms:oss-licenses-plugin:0.9.5"
        val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:2.0.0-beta01"
        val iconRibbonPlugin = "com.akaita.android:easylauncher:1.3.1"
        val gradleVersionsPlugin = "com.github.ben-manes:gradle-versions-plugin:0.22.0"
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
        val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Kotlin.coroutinesVersion}"
        val kotlinTestAssertions = "io.kotlintest:kotlintest-assertions:3.1.10"
        val testingKtx =
            "androidx.navigation:navigation-testing-ktx:${AndroidX.Navigation.version}"

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
        val appCompat = "androidx.appcompat:appcompat:1.1.0-rc01"
        val recyclerView = "androidx.recyclerview:recyclerview:1.0.0"
        val constraint = "androidx.constraintlayout:constraintlayout:2.0.0-beta2"
        val emoji = "androidx.emoji:emoji-appcompat:1.0.0"
        val design = "com.google.android.material:material:1.1.0-rc01"
        val coreKtx = "androidx.core:core-ktx:1.2.0-alpha03"
        val preference = "androidx.preference:preference:1.1.0-rc01"
        val browser = "androidx.browser:browser:1.0.0"
        val activityKtx = "androidx.activity:activity-ktx:1.1.0-alpha03"
        val fragmentKtx = "androidx.fragment:fragment-ktx:1.2.0-alpha02"

        val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.0.0"
        val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata:2.2.0-rc03"
        val liveDataCoreKtx = "androidx.lifecycle:lifecycle-livedata-core-ktx:2.2.0-rc03"
        val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0-rc03"

        object Room {
            val version = "2.2.3"
            val compiler = "androidx.room:room-compiler:$version"
            val runtime = "androidx.room:room-runtime:$version"
            val coroutine = "androidx.room:room-ktx:$version"
        }

        object Navigation {
            val version = "2.2.0-rc04"
            val runtimeKtx = "androidx.navigation:navigation-runtime-ktx:$version"
            val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:$version"
            val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"
        }

        object Work {
            val version = "2.3.0-beta02"
            val runtimeKtx = "androidx.work:work-runtime-ktx:$version"
        }
    }

    object Kotlin {
        val version = "1.3.61"
        val stdlibCommon = "org.jetbrains.kotlin:kotlin-stdlib-common:$version"
        val stdlibJvm = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$version"
        val coroutinesVersion = "1.3.3"
        val coroutinesCommon =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutinesVersion"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
        val coroutinesNative =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutinesVersion"
        val coroutinesIosX64 =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-iosx64:$coroutinesVersion"
        val coroutinesIosArm64 =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core-iosarm64:$coroutinesVersion"
        val androidCoroutinesDispatcher =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"
        val coroutinesReactive =
            "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$coroutinesVersion"
        val coroutinesPlayServices =
            "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion"
        val serializationVersion = "0.14.0"
        val serializationCommon =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion"
        val serializationAndroid =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion"
        val serializationNative =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serializationVersion"
        val serializationIosX64 =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime-iosx64:$serializationVersion"
        val serializationIosArm64 =
            "org.jetbrains.kotlinx:kotlinx-serialization-runtime-iosarm64:$serializationVersion"
    }

    object Firebase {
        val core = "com.google.firebase:firebase-core:16.0.4"
        val firestoreKtx = "com.google.firebase:firebase-firestore-ktx:20.2.0"
        val auth = "com.google.firebase:firebase-auth:18.1.0"
        val crashlytics = "com.google.firebase:firebase-crashlytics:17.0.0-beta01"
        val analytics = "com.google.firebase:firebase-analytics:17.2.2"
        val messaging = "com.google.firebase:firebase-messaging:17.3.3"
    }

    object PlayServices {
        val auth = "com.google.android.gms:play-services-auth:16.0.1"
        val licensesPlugin = "com.google.android.gms:play-services-oss-licenses:16.0.1"
    }

    object Dagger {
        val version = "2.25.4"
        val core = "com.google.dagger:dagger:$version"
        val compiler = "com.google.dagger:dagger-compiler:$version"
        val androidSupport = "com.google.dagger:dagger-android-support:$version"
        val android = "com.google.dagger:dagger-android:$version"
        val androidProcessor = "com.google.dagger:dagger-android-processor:$version"
        val assistedInjectAnnotations =
            "com.squareup.inject:assisted-inject-annotations-dagger2:0.5.2"
        val assistedInjectProcessor = "com.squareup.inject:assisted-inject-processor-dagger2:0.5.2"
    }

    object Ktor {
        val version = "1.3.0"
        val clientCommon = "io.ktor:ktor-client-core:$version"
        val clientAndroid = "io.ktor:ktor-client-okhttp:$version"
        val clientIos = "io.ktor:ktor-client-ios:$version"
        val clientIosArm64 = "io.ktor:ktor-client-ios-iosarm64:$version"
        val clientIosX64 = "io.ktor:ktor-client-ios-iosx64:$version"
        val jsonCommon = "io.ktor:ktor-client-json:$version"
        val jsonJvm = "io.ktor:ktor-client-json-jvm:$version"
        val jsonNative = "io.ktor:ktor-client-json-native:$version"
        val jsonIosArm64 = "io.ktor:ktor-client-json-iosarm64:$version"
        val jsonIosIosX64 = "io.ktor:ktor-client-json-iosx64:$version"
        val serializationCommon = "io.ktor:ktor-client-serialization:$version"
        val serializationJvm = "io.ktor:ktor-client-serialization-jvm:$version"
        val serializationNative = "io.ktor:ktor-client-serialization-native:$version"
        val serializationIosArm64 = "io.ktor:ktor-client-serialization-iosarm64:$version"
        val serializationIosX64 = "io.ktor:ktor-client-serialization-iosx64:$version"
    }

    object OkHttp {
        val version = "4.0.1"
        val client = "com.squareup.okhttp3:okhttp:$version"
        val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$version"
        val okio = "com.squareup.okio:okio:1.14.0"
    }

    val liveEvent = "com.github.hadilq.liveevent:liveevent:1.0.1"

    object LeakCanary {
        val version = "2.1"
        val leakCanary = "com.squareup.leakcanary:leakcanary-android:$version"
    }

    object Stetho {
        val stetho = "com.facebook.stetho:stetho:1.5.1"
    }

    object Hyperion {
        val version = "0.9.25"
        val hyperionPlugins = listOf(
            "com.willowtreeapps.hyperion:hyperion-core:$version",
            "com.willowtreeapps.hyperion:hyperion-attr:$version",
            "com.willowtreeapps.hyperion:hyperion-measurement:$version",
            "com.willowtreeapps.hyperion:hyperion-disk:$version",
            "com.willowtreeapps.hyperion:hyperion-recorder:$version",
            "com.willowtreeapps.hyperion:hyperion-phoenix:$version",
            "com.willowtreeapps.hyperion:hyperion-crash:$version",
            "com.willowtreeapps.hyperion:hyperion-shared-preferences:$version",
            "com.willowtreeapps.hyperion:hyperion-geiger-counter:$version",
            "com.willowtreeapps.hyperion:hyperion-build-config:$version"
        )
    }

    object Groupie {
        val version = "2.4.0-alpha1"
        val groupie = "com.xwray:groupie:$version"
        val databinding = "com.xwray:groupie-databinding:$version"
    }

    object Coil {
        val version = "0.8.0"
        val coil = "io.coil-kt:coil:$version"
    }

    object Klock {
        val version = "1.8.6"
        val common = "com.soywiz.korlibs.klock:klock:$version"
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
}
