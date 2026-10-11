// TestAnalytics and TestAnalyticsClient, which the features' tests use too.
// A separate module: Kotlin Multiplatform has no test fixtures.
plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":crossfeature:analytics"))
        }
    }
}
