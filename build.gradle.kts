//// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
//    alias(libs.plugins.androidApplication) apply false
//    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
//
//    id("com.google.gms.google-services") version "4.4.2" apply false
//}
//
//buildscript {
//    dependencies {
//        classpath("com.google.gms:google-services:4.3.10")
//        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
//    }
//}

// プロジェクトレベルの build.gradle ファイル

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false

    id("com.google.gms.google-services") version "4.4.2" apply false
}
