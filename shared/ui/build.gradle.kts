plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.compose)
}

android {
    namespace = "krio.systemdesign.shoppingapp.shared.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:compose-utils"))

    implementation(libs.coil.compose)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
}
