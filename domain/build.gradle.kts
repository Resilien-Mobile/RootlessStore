plugins {
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.kotlin.jvm)
    id("com.google.devtools.ksp")
}


dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
}
