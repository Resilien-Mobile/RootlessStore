
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}
android {
    namespace = "com.baidaidai.rootless_store.core"
    compileSdk = 37

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.ui)

    // Basic JitPack AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.documentfile)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Other
    testImplementation(libs.junit)
    implementation(libs.core)
    implementation(libs.api)
    implementation(libs.provider)

    implementation(libs.kotlinx.serialization.json)

    // Hilt Libs
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

}
