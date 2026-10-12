// The UI kit's shared app: every styled component (the design system, shared components and each feature's own
// components), the same on Android and iOS. It depends only on UI modules, never on screens or data.
// :apps:uikit:android and the Xcode project in apps/uikit/ios start it. It builds the UiKitApp framework that the
// Xcode project links (not UiKit: on a Mac's case-insensitive disk that name clashes with Apple's UIKit).
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
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "UiKitApp"
            // Static: linked into the app, nothing to embed (the setup of JetBrains' KMP template).
            isStatic = true
            binaryOption("bundleId", "krio.systemdesign.shoppingapp.uikit.UiKitApp")
        }
    }
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
