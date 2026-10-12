plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
    alias(libs.plugins.shoppingapp.kmp.compose)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:designsystem"))
            implementation(project(":core:compose-utils"))
            implementation(libs.coil.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.assertk)
        }
        // A phone set to another language: only the JVM can change the default locale in a test.
        getByName("androidHostTest").dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.assertk)
        }
    }
}
