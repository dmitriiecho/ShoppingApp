plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.hilt)
}

android {
    namespace = "krio.systemdesign.shoppingapp.core.network"
    testFixtures {
        enable = true
    }
}

dependencies {
    implementation(project(":core:config"))

    api(libs.retrofit)
    api(libs.kotlinx.serialization.json)

    implementation(libs.okhttp)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.timber)

    debugImplementation(libs.okhttp.logging)

    // networkTest: MockWebServer with a Retrofit client that reads JSON as the app does; apiSample and apiRequest
    // read server/api-samples.
    testFixturesApi(libs.okhttp.mockwebserver)
    testFixturesApi(libs.kotlinx.coroutines.test)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
}
