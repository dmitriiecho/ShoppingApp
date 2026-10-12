// The UI kit's shared app: every styled component (the design system, shared components and each feature's own
// components), the same on Android and iOS. It depends only on UI modules, never on screens or data.
// :apps:uikit:android starts it.
plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
    alias(libs.plugins.shoppingapp.kmp.compose)
    alias(libs.plugins.kotlin.serialization)
}

// A part of the app: its code and Res live in the app's package (the applicationId), not in the module's.
compose.resources {
    packageOfResClass = "krio.systemdesign.shoppingapp.uikit.resources"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:designsystem"))
            implementation(project(":crossfeature:ui"))
            implementation(project(":feature:catalog:ui"))
            implementation(project(":feature:cart:ui"))
            implementation(project(":feature:promo:ui"))
            implementation(project(":feature:checkout:ui"))

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.kotlinx.serialization.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}
