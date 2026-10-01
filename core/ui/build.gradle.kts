plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.compose)
}

android {
    namespace = "krio.systemdesign.shoppingapp.core.ui"
}

dependencies {
    // Для ProductImage: картинки товаров загружает Coil. Сетевой загрузчик (coil-network-okhttp) подключают фичи.
    implementation(libs.coil.compose)
}
