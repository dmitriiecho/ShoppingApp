plugins {
    alias(libs.plugins.shoppingapp.kmp.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

// DeepLinkConfig.BASE_URL is a const, inlined at compile time, so the analysis sees no use of :core:config;
// the code still doesn't compile without it.
dependencyAnalysis {
    issues {
        onUnusedDependencies {
            exclude(":core:config")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:cart:ui"))
            implementation(project(":crossfeature:domain"))
            implementation(project(":crossfeature:ui"))
            implementation(project(":crossfeature:analytics"))
            implementation(project(":core:designsystem"))
            implementation(project(":core:compose-utils"))
            implementation(project(":core:config"))

            implementation(libs.kotlinx.serialization.json)
            // ImmutableList in CartUiState, so Compose can compare an item's issues by content.
            implementation(libs.kotlinx.collections.immutable)
        }
        commonTest.dependencies {
            implementation(project(":crossfeature:domain:testing"))
            implementation(project(":crossfeature:analytics:testing"))
            implementation(project(":core:compose-utils:testing"))
            implementation(libs.kotlin.test)
            implementation(libs.assertk)
            implementation(libs.turbine)
        }
    }
}
