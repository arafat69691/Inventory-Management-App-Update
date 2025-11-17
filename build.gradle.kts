// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.6" apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies{
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.6")

    }
}
