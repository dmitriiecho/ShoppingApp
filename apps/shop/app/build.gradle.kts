// The shop's shared app: what Android and iOS show the same way. For now only the iOS side and a placeholder
// screen; the navigation host and the app's graph move here from :apps:shop later on the kmp branch.
// It builds the ShopApp framework that the Xcode project in apps/shop/ios links.
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.shoppingapp.kmp.compose)
}

kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "ShopApp"
            // Static: linked into the app, nothing to embed (the setup of JetBrains' KMP template).
            isStatic = true
            binaryOption("bundleId", "krio.systemdesign.shoppingapp.ShopApp")
        }
    }
}
