plugins {
    alias(libs.plugins.shoppingapp.android.feature)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.settings"
}

dependencies {
    implementation(project(":core:config"))
}
