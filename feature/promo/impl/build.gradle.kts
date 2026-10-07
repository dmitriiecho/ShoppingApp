plugins {
    alias(libs.plugins.shoppingapp.android.feature)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.promo.impl"
}

dependencies {
    implementation(project(":feature:promo:ui"))
    implementation(project(":shared:domain"))
    implementation(project(":shared:analytics"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:compose-utils"))
    implementation(project(":core:network"))

    implementation(libs.timber)
}
