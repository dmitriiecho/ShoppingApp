plugins {
    alias(libs.plugins.shoppingapp.jvm.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shoppingapp.metro)
    // Test data builders and test doubles that the features' tests use too.
    `java-test-fixtures`
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
}
