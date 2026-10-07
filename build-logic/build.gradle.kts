import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `kotlin-dsl`
}

dependencies {
    // Only AGP types for the android block; the root build.gradle.kts applies the plugins themselves.
    compileOnly(libs.android.gradlePlugin)
    // The same for the module rules: only the types of their extension.
    compileOnly(libs.module.graph.assertion.gradlePlugin)
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
        register("androidHilt") {
            id = libs.plugins.shoppingapp.android.hilt.get().pluginId
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.shoppingapp.android.feature.get().pluginId
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}
