import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

// Compose Multiplatform and its core libraries in a KMP module. Apply after shoppingapp.kmp.library, or after the
// Kotlin Multiplatform plugin in an iOS-only module (:apps:shop:shared).
class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.plugins.compose.multiplatform.get().pluginId)
            pluginManager.apply(libs.plugins.kotlin.compose.get().pluginId)

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.getByName("commonMain").dependencies {
                    implementation(libs.jetbrains.compose.runtime)
                    implementation(libs.jetbrains.compose.foundation)
                    implementation(libs.jetbrains.compose.ui)
                    implementation(libs.jetbrains.compose.material3)
                    implementation(libs.jetbrains.compose.components.resources)
                    implementation(libs.jetbrains.compose.ui.tooling.preview)
                }
            }
            // The plugin copies the module's resources next to the iOS test executable, so that tests could read
            // them. No common test reads them (ViewModel tests compare StringResource, the screenshots run on
            // Android), and the copy shares the link task's output folder, which stops Gradle from caching that
            // link: minutes on every CI run. A module whose iOS tests read resources enables these tasks again.
            tasks.named { it.startsWith("copyTestComposeResourcesFor") }.configureEach { enabled = false }

            // Res, the strings and images of the module, in the module's own package.
            extensions.getByType(ComposeExtension::class.java).extensions.configure<ResourcesExtension> {
                packageOfResClass = defaultNamespace() + ".resources"
            }

            pluginManager.withPlugin(libs.plugins.android.kotlin.multiplatform.library.get().pluginId) {
                extensions.configure<KotlinMultiplatformExtension> {
                    targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
                        // Res works on Android only with Android resources on.
                        androidResources.enable = true
                    }
                }
                dependencies {
                    // For the previews in Android Studio only: the plugin has no debug build to add it to.
                    "androidRuntimeClasspath"(libs.jetbrains.compose.ui.tooling)
                    "lintChecks"(libs.compose.lint.checks)
                }
            }
        }
    }
}
