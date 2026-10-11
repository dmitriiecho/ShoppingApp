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
    implementation(libs.shoppingapp.contract)

    implementation(libs.kermit)

    testImplementation(project(":crossfeature:analytics:testing"))
    testImplementation(testFixtures(project(":core:compose-utils")))
    testImplementation(project(":core:network:testing"))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.turbine)
}
