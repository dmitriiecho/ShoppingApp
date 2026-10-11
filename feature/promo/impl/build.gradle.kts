plugins {
    alias(libs.plugins.shoppingapp.android.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.promo.impl"
}

dependencies {
    implementation(project(":feature:promo:ui"))
    implementation(project(":crossfeature:domain"))
    implementation(project(":crossfeature:analytics"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:compose-utils"))
    implementation(project(":core:network"))

    implementation(libs.kermit)

    testImplementation(testFixtures(project(":crossfeature:analytics")))
    testImplementation(testFixtures(project(":core:compose-utils")))
    testImplementation(testFixtures(project(":core:network")))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.turbine)
}
