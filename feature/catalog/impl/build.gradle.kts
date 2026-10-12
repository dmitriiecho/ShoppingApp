plugins {
    alias(libs.plugins.shoppingapp.android.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.catalog.impl"
}

dependencies {
    implementation(project(":feature:catalog:ui"))
    implementation(project(":crossfeature:domain"))
    implementation(project(":crossfeature:ui"))
    implementation(project(":crossfeature:analytics"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:compose-utils"))
    implementation(project(":core:config"))
    implementation(project(":core:network"))
    implementation(libs.shoppingapp.contract)

    implementation(libs.androidx.paging.compose)

    testImplementation(project(":crossfeature:domain:testing"))
    testImplementation(project(":crossfeature:analytics:testing"))
    testImplementation(project(":core:compose-utils:testing"))
    testImplementation(project(":core:network:testing"))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
