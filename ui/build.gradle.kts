
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}
android {

    namespace = "com.baidaidai.rootless_store.ui"
    compileSdk = 37

    buildFeatures {
        compose = true
    }
}

dependencies {

    // Basic JitPack AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime.saveable)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material3.window.size.class1)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.splashscreen) // Only MDC For SplashScreen

    // Other
    testImplementation(libs.junit)
    implementation(libs.core)
    implementation(libs.api)
    implementation(libs.provider)
    implementation(libs.material)

    implementation(libs.kotlinx.serialization.json)

    // Hilt Libs
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Paging Libs
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose) // Optional

    // Coil Libs
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor2)

    // Navigation3
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)
    ksp(libs.kotlin.metadata.jvm)

    // Illusion Cube
    implementation(project(":application"))
    implementation(project(":domain"))
    implementation(project(":core"))


    // WebView
    implementation("androidx.webkit:webkit:1.16.0")

    // Adaptive
    implementation("androidx.compose.material3.adaptive:adaptive:1.3.0-rc01")

}
