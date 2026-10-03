import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

// Hilt с генерацией кода через KSP. Применяется после library или application.
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.plugins.ksp.get().pluginId)
            pluginManager.apply(libs.plugins.hilt.get().pluginId)

            dependencies {
                "implementation"(libs.hilt.android)
                "ksp"(libs.hilt.compiler)
            }
        }
    }
}
