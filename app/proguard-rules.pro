# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# GIF Library
-keep class pl.droidsonroids.gif.** { *; }
-dontwarn pl.droidsonroids.gif.**

# GSON
-keep class com.google.gson.stream.** { *; }
-keep class com.romaster.rwm.** { *; }

# Kotlin
-keep class kotlin.** { *; }
-dontwarn kotlin.**

# Material Components
-keep class com.google.android.material.** { *; }
-keep public class * extends com.google.android.material.R
-keep public class * extends com.google.android.material.internal.**

# AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**