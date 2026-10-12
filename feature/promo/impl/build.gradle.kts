plugins {
    alias(libs.plugins.shoppingapp.kmp.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:promo:ui"))
            implementation(project(":crossfeature:domain"))
            implementation(project(":crossfeature:analytics"))
            implementation(project(":core:designsystem"))
            implementation(project(":core:compose-utils"))
            implementation(project(":core:network"))
            implementation(libs.shoppingapp.contract)

            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(project(":crossfeature:analytics:testing"))
            implementation(project(":core:compose-utils:testing"))
            implementation(project(":core:network:testing"))
            implementation(libs.kotlin.test)
            implementation(libs.assertk)
            implementation(libs.turbine)
        }
    }
}
