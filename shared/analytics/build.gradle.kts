// Pure Kotlin, so it can move to KMP commonMain as is.
plugins {
    alias(libs.plugins.shoppingapp.jvm.library)
    // TestAnalytics and TestAnalyticsClient, which the features' tests use too.
    `java-test-fixtures`
}

dependencies {
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
}
