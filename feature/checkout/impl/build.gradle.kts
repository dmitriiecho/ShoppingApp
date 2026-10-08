plugins {
    alias(libs.plugins.shoppingapp.android.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
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

    testImplementation(testFixtures(project(":shared:domain")))
    testImplementation(testFixtures(project(":shared:analytics")))
    testImplementation(testFixtures(project(":core:compose-utils")))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.turbine)
}
