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
            implementation(project(":feature:catalog:ui"))
            implementation(project(":crossfeature:domain"))
            implementation(project(":crossfeature:ui"))
            implementation(project(":crossfeature:analytics"))
            implementation(project(":core:designsystem"))
            implementation(project(":core:compose-utils"))
            implementation(project(":core:config"))
            implementation(project(":core:network"))
            implementation(libs.shoppingapp.contract)

            implementation(libs.androidx.paging.compose)
        }
        commonTest.dependencies {
            implementation(project(":crossfeature:domain:testing"))
            implementation(project(":crossfeature:analytics:testing"))
            implementation(project(":core:compose-utils:testing"))
            implementation(project(":core:network:testing"))
            implementation(libs.kotlin.test)
            implementation(libs.assertk)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}
