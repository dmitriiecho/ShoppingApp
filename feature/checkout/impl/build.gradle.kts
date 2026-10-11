plugins {
    alias(libs.plugins.shoppingapp.android.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.checkout.impl"
}

dependencies {
    implementation(project(":feature:checkout:ui"))
    implementation(project(":crossfeature:domain"))
    implementation(project(":crossfeature:ui"))
    implementation(project(":crossfeature:analytics"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:compose-utils"))

    testImplementation(testFixtures(project(":crossfeature:domain")))
    testImplementation(testFixtures(project(":crossfeature:analytics")))
    testImplementation(testFixtures(project(":core:compose-utils")))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.turbine)
}
