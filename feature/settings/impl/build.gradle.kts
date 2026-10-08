plugins {
    alias(libs.plugins.shoppingapp.android.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.settings.impl"
}

dependencies {
    implementation(project(":shared:domain"))
    implementation(project(":shared:analytics"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:compose-utils"))
    implementation(project(":core:config"))
}
