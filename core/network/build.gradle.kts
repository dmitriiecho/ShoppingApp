plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shoppingapp.android.hilt)
}

android {
    namespace = "krio.systemdesign.shoppingapp.core.network"
}

dependencies {
    implementation(project(":core:config"))

    implementation(libs.okhttp)
    debugImplementation(libs.okhttp.logging)
    api(libs.retrofit)
    api(libs.retrofit.converter.kotlinx.serialization)
    api(libs.kotlinx.serialization.json)
}
