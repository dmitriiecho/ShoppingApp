// Каталог компонентов :core:ui — отдельное приложение, в Android Studio запускается своей run configuration.
// Из модулей проекта зависит только от :core:ui: всё, что он показывает, должно лежать там.
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
    implementation(project(":core:ui"))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
}
