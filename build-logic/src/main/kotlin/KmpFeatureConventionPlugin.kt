import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

// A feature's impl module on KMP: Compose screens, ViewModels injected by Metro, kotlinx.serialization navigation
// routes, all in commonMain. Each feature declares its own project dependencies (:crossfeature:domain,
// :core:designsystem…).
class KmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply<KmpLibraryConventionPlugin>()
            apply<KmpComposeConventionPlugin>()
            pluginManager.apply(libs.plugins.kotlin.serialization.get().pluginId)
            apply<MetroConventionPlugin>()

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.getByName("commonMain").dependencies {
                    implementation(libs.jetbrains.navigation.compose)
                    implementation(libs.jetbrains.lifecycle.runtime.compose)
                    implementation(libs.jetbrains.lifecycle.viewmodel.compose)
                    implementation(libs.kotlinx.serialization.core)
                }
            }
        }
    }
}
