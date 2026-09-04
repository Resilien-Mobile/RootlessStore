
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.serialization)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}
android {


    namespace = "com.baidaidai.rootless_store.service"
    compileSdk = 37
}

dependencies {

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
    implementation(libs.material)

    implementation(libs.kotlinx.serialization.json)

    // Hilt Libs
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)


    // Illusion Cube
    implementation(project(":application"))
    implementation(project(":domain"))

    // WebView

    // Adaptive

}
