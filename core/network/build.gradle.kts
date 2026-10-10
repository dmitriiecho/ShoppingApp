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

    api(libs.ktor.client.core)
    api(libs.kotlinx.serialization.json)

    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kermit)

    debugImplementation(libs.ktor.client.logging)

    // networkTest: a client with the app's settings over TestServer; apiSample and apiRequest read server/api-samples.
    testFixturesApi(libs.ktor.client.mock)
    testFixturesApi(libs.kotlinx.coroutines.test)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
}
