// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Add the ksp support
    // Kotlin Symbol Processing
    id("com.google.devtools.ksp") version "2.3.2" apply false

    // Add the Hilt(DI) support
    id("com.google.dagger.hilt.android") version "2.59.2" apply false
}