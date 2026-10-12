plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
    alias(libs.plugins.shoppingapp.kmp.compose)
    alias(libs.plugins.shoppingapp.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
        }
    }
}
