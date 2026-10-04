plugins {
    alias(libs.plugins.shoppingapp.android.feature)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.cart"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:config"))
}
