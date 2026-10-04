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
}

dependencies {
    implementation(project(":feature:catalog"))
    implementation(project(":feature:cart"))
    implementation(project(":feature:promo"))
    implementation(project(":feature:checkout"))
    implementation(project(":feature:settings"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":core:ui"))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Lets Coil load product images over the network (ProductImage in :core:ui).
    implementation(libs.coil.network.okhttp)
}
