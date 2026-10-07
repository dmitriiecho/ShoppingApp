plugins {
    alias(libs.plugins.shoppingapp.android.feature)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.catalog.impl"
}

dependencies {
    implementation(project(":feature:catalog:ui"))
    implementation(project(":shared:domain"))
    implementation(project(":shared:ui"))
    implementation(project(":shared:analytics"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:compose-utils"))
    implementation(project(":core:config"))
    implementation(project(":core:network"))

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}
