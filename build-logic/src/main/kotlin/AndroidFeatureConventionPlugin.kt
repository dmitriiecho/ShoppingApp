import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

// A feature's impl module: Compose screens, Hilt ViewModels, kotlinx.serialization navigation routes.
// Each feature declares its own project dependencies (:shared:domain, :core:designsystem…).
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply<AndroidLibraryConventionPlugin>()
            apply<AndroidComposeConventionPlugin>()
            pluginManager.apply(libs.plugins.kotlin.serialization.get().pluginId)
            apply<AndroidHiltConventionPlugin>()

            dependencies {
                "implementation"(libs.androidx.hilt.lifecycle.viewmodel.compose)
                "implementation"(libs.androidx.navigation.compose)
                "implementation"(libs.androidx.lifecycle.runtime.compose)
                "implementation"(libs.androidx.lifecycle.viewmodel.compose)
                "implementation"(libs.kotlinx.serialization.core)
            }
        }
    }
}
