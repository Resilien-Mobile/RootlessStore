// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android Application
    alias(libs.plugins.android.application) apply false
    // Android ARchive AAR
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false


    // Add the ksp support
    // Kotlin Symbol Processing
    id("com.google.devtools.ksp") version "2.3.2" apply false

    // Add the Hilt(DI) support
    id("com.google.dagger.hilt.android") version "2.59.2" apply false
}