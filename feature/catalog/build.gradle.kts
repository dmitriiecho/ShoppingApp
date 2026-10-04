plugins {
    alias(libs.plugins.shoppingapp.android.feature)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.catalog"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:config"))
    implementation(project(":core:network"))

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}
