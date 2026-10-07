// The UI kit, a separate app showing every styled component: the design system, shared components
// and each feature's own components. It depends only on UI modules, never on screens or data.
plugins {
    alias(libs.plugins.shoppingapp.android.application)
    alias(libs.plugins.shoppingapp.android.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "krio.systemdesign.shoppingapp.uikit"

    defaultConfig {
        applicationId = "krio.systemdesign.shoppingapp.uikit"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":shared:ui"))
    implementation(project(":feature:catalog:ui"))
    implementation(project(":feature:cart:ui"))
    implementation(project(":feature:promo:ui"))
    implementation(project(":feature:checkout:ui"))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
}
