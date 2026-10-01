plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.hilt)
}

android {
    namespace = "krio.systemdesign.shoppingapp.core.config"

    defaultConfig {
        buildConfigField(
            "String",
            "DEEP_LINK_HOST",
            "\"${providers.gradleProperty("deepLinkHost").get()}\"",
        )
    }

    buildFeatures {
        buildConfig = true
    }
}
