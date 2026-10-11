plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shoppingapp.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.assertk)
            implementation(project(":crossfeature:domain:testing"))
        }
    }
}
