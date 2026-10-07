plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.compose)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.catalog.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
}
