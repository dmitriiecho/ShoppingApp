plugins {
    alias(libs.plugins.shoppingapp.kmp.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

// DeepLinkConfig.TEST_PAGES_URL is a const, inlined at compile time, so the analysis sees no use of :core:config;
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
            implementation(project(":crossfeature:domain"))
            implementation(project(":crossfeature:analytics"))
            implementation(project(":core:designsystem"))
            implementation(project(":core:compose-utils"))
            implementation(project(":core:config"))
        }
    }
}
