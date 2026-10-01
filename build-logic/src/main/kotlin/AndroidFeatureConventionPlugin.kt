import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

// Фича: экраны на Compose, ViewModel на Hilt, маршруты навигации через kotlinx.serialization.
// Зависимости на модули проекта (:domain, :core:ui…) фича объявляет сама: у разных фич они разные.
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply<AndroidLibraryConventionPlugin>()
            apply<AndroidComposeConventionPlugin>()
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
            apply<AndroidHiltConventionPlugin>()

            dependencies {
                "implementation"(libs.findLibrary("androidx-hilt-navigation-compose").get())
                "implementation"(libs.findLibrary("androidx-navigation-compose").get())
                "implementation"(libs.findLibrary("androidx-lifecycle-runtime-compose").get())
                "implementation"(libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
                "implementation"(libs.findLibrary("kotlinx-serialization-json").get())
                "implementation"(libs.findLibrary("kotlinx-coroutines-android").get())
            }
        }
    }
}
