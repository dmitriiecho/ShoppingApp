// The shop's Android app: the entry point (MainActivity, ShoppingApp), the app's dependency graph for Android and
// what only an Android app has (manifest, themes, icons). The screens and navigation are in :apps:shop:app.
plugins {
    alias(libs.plugins.shoppingapp.android.application)
    alias(libs.plugins.shoppingapp.android.compose)
    alias(libs.plugins.shoppingapp.metro)
}

// The APK keeps the name it had before the app moved to apps/shop/android (shop-release.apk), as Latest build expects.
base {
    archivesName = "shop"
}

android {
    namespace = "krio.systemdesign.shoppingapp"

    defaultConfig {
        applicationId = "krio.systemdesign.shoppingapp"
        versionCode = 1
        versionName = "1.0"
    }

    // The strings are Compose Multiplatform resources, which the generated list doesn't see: the app's languages
    // are listed by hand in res/xml/locale_config.xml.
    androidResources {
        generateLocaleConfig = false
    }

    // Room's own SQLite (BundledSQLiteDriver) is a native library, one copy per processor type. Compressed in the APK:
    // the universal APK from the README is downloaded whole, 4.8 MB instead of 7.3 MB. Android unpacks the copy for
    // its processor on install.
    packaging {
        jniLibs.useLegacyPackaging = true
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":apps:shop:app"))
    // Not used by this module's code: the app's graph (AndroidAppGraph) sees only the modules on its compile classpath,
    // so every module with contributions or with classes the graph creates is a direct dependency.
    implementation(project(":feature:catalog:impl"))
    implementation(project(":feature:cart:impl"))
    implementation(project(":feature:promo:impl"))
    implementation(project(":feature:checkout:impl"))
    implementation(project(":feature:settings:impl"))
    implementation(project(":crossfeature:data"))
    implementation(project(":crossfeature:domain"))
    implementation(project(":crossfeature:analytics"))
    implementation(project(":core:compose-utils"))
    implementation(project(":core:network"))
    // The window background color of the themes.
    implementation(project(":core:designsystem"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core)
    implementation(libs.kermit)
    // Request logging, in debug only (HttpLogging in src/debug).
    debugImplementation(libs.ktor.client.logging)
}
