# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/share/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontobfuscate

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

# AWS
# Class names are needed in reflection
-keepnames class com.amazonaws.**
# Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler
# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**
# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**


# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn java.nio.**
-dontwarn org.slf4j.**
-dontwarn javax.annotation.**
-dontwarn java.beans.**
-dontwarn retrofit2.Platform$Java8

-keep class okhttp3.** { *; }

# Glide
-keep class com.bumptech.glide.** { *; }