import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

// Compose и его основные библиотеки. Применяется после library или application: ему нужен блок android.
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.plugins.kotlin.compose.get().pluginId)

            extensions.configure<CommonExtension> {
                buildFeatures.compose = true
            }

            dependencies {
                "implementation"(platform(libs.androidx.compose.bom))
                "implementation"(libs.androidx.compose.material3)
                "implementation"(libs.androidx.compose.ui.asProvider())
                "implementation"(libs.androidx.compose.ui.graphics)
                "implementation"(libs.androidx.compose.ui.tooling.preview)
                "debugImplementation"(libs.androidx.compose.ui.tooling.asProvider())
            }
        }
    }
}
