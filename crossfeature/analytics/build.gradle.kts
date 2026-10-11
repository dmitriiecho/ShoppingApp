plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.assertk)
            implementation(project(":crossfeature:analytics:testing"))
        }
    }
}
