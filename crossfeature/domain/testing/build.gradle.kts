// Test data builders and test doubles for the domain, which the tests of other modules use too.
// A separate module: Kotlin Multiplatform has no test fixtures.
plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":crossfeature:domain"))
        }
    }
}
