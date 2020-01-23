# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

## Extra

# Generate merge config list
-printconfiguration proguard-merged-config.txt

## General

# Do not remove annotations
-keepattributes *Annotation*
-keepattributes EnclosingMethod,Signature

# Replace source file attributes by SourceFile to reduce the size
# report system can de-obfuscate them
-renamesourcefileattribute SourceFile
# To see readable stacktraces
-keepattributes SourceFile,LineNumberTable

-dontwarn org.jetbrains.annotations.**


# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# TODO: Works but needs to be obfuscated
-keep class io.github.droidkaigi.confsched2020.staff.** { *; }
-keep class io.github.droidkaigi.confsched2020.contributor.** { *; }

# NOTE: staff and contributor settings
# # reflect
# -keep class kotlin.reflect.** { *; }
# -keep class kotlin.jvm.internal.** { *; }
# -keep class kotlin.jvm.functions.** { *; }
# -keep class androidx.lifecycle.ViewModel
# -keep class androidx.fragment.app.** { *; }
# -keep class io.github.droidkaigi.confsched2020.util.** { *; }
