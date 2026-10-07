plugins {
    alias(libs.plugins.shoppingapp.android.application)
    alias(libs.plugins.shoppingapp.android.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shoppingapp.android.hilt)
}

android {
    namespace = "krio.systemdesign.shoppingapp"

    defaultConfig {
        applicationId = "krio.systemdesign.shoppingapp"
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":feature:catalog:impl"))
    implementation(project(":feature:cart:impl"))
    implementation(project(":feature:promo:impl"))
    implementation(project(":feature:checkout:impl"))
    implementation(project(":feature:settings:impl"))
    implementation(project(":shared:data"))
    implementation(project(":shared:domain"))
    implementation(project(":shared:ui"))
    implementation(project(":shared:analytics"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:compose-utils"))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.timber)

    // Lets Coil load product images over the network (ProductImage in :shared:ui).
    implementation(libs.coil.network.okhttp)
}
