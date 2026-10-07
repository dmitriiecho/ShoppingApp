plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.compose)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.checkout.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":shared:ui"))
}
