import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

// An Android library with the shared compileSdk, minSdk and Java version.
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.plugins.android.library.get().pluginId)
            pluginManager.apply(libs.plugins.dependency.analysis.get().pluginId)

            extensions.configure<LibraryExtension> {
                configureAndroid(this)
            }

            // Kotlin LSP ("Kotlin by JetBrains") adds R.jar only to app modules, so R is unresolved in libraries.
            // The property is set only by its project import; R.jar exists after the module's first build.
            if (providers.systemProperty("com.jetbrains.ls.imports.gradle").isPresent) {
                dependencies {
                    add(
                        "compileOnly",
                        files(
                            layout.buildDirectory.file(
                                "intermediates/compile_r_class_jar/debug/generateDebugRFile/R.jar",
                            ),
                        ),
                    )
                }
            }
        }
    }
}
