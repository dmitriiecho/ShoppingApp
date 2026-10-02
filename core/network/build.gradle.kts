plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "krio.systemdesign.shoppingapp.core.network"
}

dependencies {
    implementation(project(":core:config"))

    api(libs.retrofit)
    api(libs.kotlinx.serialization.json)

    implementation(libs.okhttp)
    implementation(libs.retrofit.converter.kotlinx.serialization)

    debugImplementation(libs.okhttp.logging)
}
