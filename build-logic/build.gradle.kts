import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `kotlin-dsl`
}

dependencies {
    // Only AGP and Kotlin types for the android and kotlin blocks; the root build.gradle.kts applies the plugins
    // themselves.
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    // The same for the module rules: only the types of their extension.
    compileOnly(libs.module.graph.assertion.gradlePlugin)
    // And Metro's settings (MetroConventionPlugin).
    compileOnly(libs.metro.gradlePlugin)
    // And the screenshot tests' settings (AndroidScreenshotsConventionPlugin).
    compileOnly(libs.roborazzi.gradlePlugin)
    // And the Compose Multiplatform settings (KmpComposeConventionPlugin).
    compileOnly(libs.compose.multiplatform.gradlePlugin)
    // Lets convention plugins use the type-safe libs accessors (gradle/gradle#15383).
    implementation(files(LibrariesForLibs::class.java.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.shoppingapp.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.shoppingapp.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = libs.plugins.shoppingapp.android.compose.get().pluginId
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("metro") {
            id = libs.plugins.shoppingapp.metro.get().pluginId
            implementationClass = "MetroConventionPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.shoppingapp.android.feature.get().pluginId
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidScreenshots") {
            id = libs.plugins.shoppingapp.android.screenshots.get().pluginId
            implementationClass = "AndroidScreenshotsConventionPlugin"
        }
        register("kmpLibrary") {
            id = libs.plugins.shoppingapp.kmp.library.get().pluginId
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = libs.plugins.shoppingapp.kmp.compose.get().pluginId
            implementationClass = "KmpComposeConventionPlugin"
        }
        register("kmpFeature") {
            id = libs.plugins.shoppingapp.kmp.feature.get().pluginId
            implementationClass = "KmpFeatureConventionPlugin"
        }
    }
}
