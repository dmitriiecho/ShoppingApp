plugins {
    alias(libs.plugins.shoppingapp.android.feature)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.cart.impl"
}

dependencies {
    implementation(project(":feature:cart:ui"))
    implementation(project(":shared:domain"))
    implementation(project(":shared:ui"))
    implementation(project(":shared:analytics"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:compose-utils"))
    implementation(project(":core:config"))

    implementation(libs.kotlinx.serialization.json)
    // ImmutableList in CartUiState, so Compose can compare an item's issues by content.
    implementation(libs.kotlinx.collections.immutable)

    testImplementation(testFixtures(project(":shared:domain")))
    testImplementation(testFixtures(project(":shared:analytics")))
    testImplementation(testFixtures(project(":core:compose-utils")))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.turbine)
}
