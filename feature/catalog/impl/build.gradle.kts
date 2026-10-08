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

    implementation(libs.androidx.paging.compose)

    testImplementation(testFixtures(project(":shared:domain")))
    testImplementation(testFixtures(project(":shared:analytics")))
    testImplementation(testFixtures(project(":core:compose-utils")))
    testImplementation(testFixtures(project(":core:network")))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.paging.testing)
}
