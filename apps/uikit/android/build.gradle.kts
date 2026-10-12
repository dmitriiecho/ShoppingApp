// The UI kit's Android app: the entry point (UiKitActivity) and what only an Android app has (manifest, themes, icons).
// The screens and samples are in :apps:uikit:app.
plugins {
    alias(libs.plugins.shoppingapp.android.application)
    alias(libs.plugins.shoppingapp.android.compose)
}

// The APK keeps the name it had before the app moved to apps/uikit/android (uikit-release.apk), as Latest build expects.
base {
    archivesName = "uikit"
}

android {
    namespace = "krio.systemdesign.shoppingapp.uikit"

    defaultConfig {
        applicationId = "krio.systemdesign.shoppingapp.uikit"
        versionCode = 1
        versionName = "1.0"
    }

    // The strings are Compose Multiplatform resources, which the generated list doesn't see: the app's languages
    // are listed by hand in res/xml/locale_config.xml.
    androidResources {
        generateLocaleConfig = false
    }
}

dependencies {
    implementation(project(":apps:uikit:app"))
    // The window background in the app's theme is the design system's color.
    implementation(project(":core:designsystem"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core)
}
