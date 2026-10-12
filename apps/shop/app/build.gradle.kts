// The shop's shared app: the navigation host, the bottom bar, analytics and the root of the UI, the same on Android
// and iOS. :apps:shop:android and the Xcode project in apps/shop/ios start it. It builds the ShopApp framework that
// the Xcode project links.
plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
    alias(libs.plugins.shoppingapp.kmp.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shoppingapp.metro)
}

// A part of the app: its code and Res live in the app's package (the applicationId), not in the module's.
compose.resources {
    packageOfResClass = "krio.systemdesign.shoppingapp.resources"
}

// PRODUCT_IMAGE_TRANSITION_MILLIS is a const, inlined at compile time, so the analysis sees no use of
// :crossfeature:ui; the code still doesn't compile without it.
dependencyAnalysis {
    issues {
        onUnusedDependencies {
            exclude(":crossfeature:ui")
        }
    }
}

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "ShopApp"
            // Static: linked into the app, nothing to embed (the setup of JetBrains' KMP template).
            isStatic = true
            binaryOption("bundleId", "krio.systemdesign.shoppingapp.ShopApp")
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:catalog:impl"))
            implementation(project(":feature:cart:impl"))
            implementation(project(":feature:promo:impl"))
            implementation(project(":feature:checkout:impl"))
            implementation(project(":feature:settings:impl"))
            implementation(project(":crossfeature:domain"))
            implementation(project(":crossfeature:ui"))
            implementation(project(":crossfeature:analytics"))
            implementation(project(":core:designsystem"))
            implementation(project(":core:compose-utils"))

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            // Lets Coil load product images over the network (ProductImage in :crossfeature:ui), with the app's
            // HttpClient: App() sets Coil's image loader.
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.assertk)
        }
    }
}
