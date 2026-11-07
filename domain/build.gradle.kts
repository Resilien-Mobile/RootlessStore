plugins {
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.kotlin.jvm)
    id("com.google.devtools.ksp")
}


dependencies {
    implementation(libs.androidx.paging.common)
    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)

    // kotlin-result
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.3.1")
}
