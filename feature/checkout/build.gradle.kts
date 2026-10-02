plugins {
    alias(libs.plugins.shoppingapp.android.feature)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.checkout"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:ui"))
    // Для ProductImage: картинки товаров по сети грузит Coil.
    implementation(libs.coil.network.okhttp)
}
