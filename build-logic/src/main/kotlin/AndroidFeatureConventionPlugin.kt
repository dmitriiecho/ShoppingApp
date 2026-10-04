import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

// A feature module: Compose screens, Hilt ViewModels, kotlinx.serialization navigation routes.
// Each feature declares its own project dependencies (:domain, :core:ui…).
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply<AndroidLibraryConventionPlugin>()
            apply<AndroidComposeConventionPlugin>()
            pluginManager.apply(libs.plugins.kotlin.serialization.get().pluginId)
            apply<AndroidHiltConventionPlugin>()

            dependencies {
                "implementation"(libs.androidx.hilt.navigation.compose)
                "implementation"(libs.androidx.navigation.compose)
                "implementation"(libs.androidx.lifecycle.runtime.compose)
                "implementation"(libs.androidx.lifecycle.viewmodel.compose)
                "implementation"(libs.kotlinx.serialization.json)
                "implementation"(libs.kotlinx.coroutines.android)
            }
        }
    }
}
