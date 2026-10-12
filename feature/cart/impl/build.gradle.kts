plugins {
    alias(libs.plugins.shoppingapp.android.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.cart.impl"
}

dependencies {
    implementation(project(":feature:cart:ui"))
    implementation(project(":crossfeature:domain"))
    implementation(project(":crossfeature:ui"))
    implementation(project(":crossfeature:analytics"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:compose-utils"))
    implementation(project(":core:config"))

    implementation(libs.kotlinx.serialization.json)
    // ImmutableList in CartUiState, so Compose can compare an item's issues by content.
    implementation(libs.kotlinx.collections.immutable)

    testImplementation(project(":crossfeature:domain:testing"))
    testImplementation(project(":crossfeature:analytics:testing"))
    testImplementation(project(":core:compose-utils:testing"))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.turbine)
}
