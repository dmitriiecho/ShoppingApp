plugins {
    alias(libs.plugins.shoppingapp.android.feature)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.checkout"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:ui"))
}
