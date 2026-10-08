// Pure Kotlin, so it can move to KMP commonMain as is.
plugins {
    alias(libs.plugins.shoppingapp.jvm.library)
    // A test analytics client that the features' tests use too.
    `java-test-fixtures`
}

dependencies {
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
}
