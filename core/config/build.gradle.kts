plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "krio.systemdesign.shoppingapp.core.config"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 26
        buildConfigField(
            "String",
            "DEEP_LINK_HOST",
            "\"${providers.gradleProperty("deepLinkHost").get()}\"",
        )
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
