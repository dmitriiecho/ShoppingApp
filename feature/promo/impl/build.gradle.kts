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

    testImplementation(testFixtures(project(":shared:analytics")))
    testImplementation(testFixtures(project(":core:compose-utils")))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.turbine)
}
