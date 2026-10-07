plugins {
    alias(libs.plugins.shoppingapp.android.feature)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.checkout.impl"
}

dependencies {
    implementation(project(":feature:checkout:ui"))
    implementation(project(":shared:domain"))
    implementation(project(":shared:ui"))
    implementation(project(":shared:analytics"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:compose-utils"))
}
