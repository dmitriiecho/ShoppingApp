plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
    alias(libs.plugins.shoppingapp.kmp.compose)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

kotlin {
    sourceSets {
        // SystemBars: the status and navigation bar icons of an Android window.
        androidMain.dependencies {
            implementation(libs.androidx.core)
        }
    }
}
