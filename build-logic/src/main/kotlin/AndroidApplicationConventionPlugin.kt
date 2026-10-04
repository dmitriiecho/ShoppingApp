import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// An app module: the library setup plus targetSdk.
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.plugins.android.application.get().pluginId)

            extensions.configure<ApplicationExtension> {
                configureAndroid(this)
                defaultConfig.targetSdk = AndroidConfig.TARGET_SDK

                buildTypes.getByName("release") {
                    // Demo apps: release is signed with the debug key so it installs on an emulator, and isn't shrunk.
                    // Publishing to a store would need a real key.
                    signingConfig = signingConfigs.getByName("debug")
                    optimization {
                        enable = false
                    }
                }
            }
        }
    }
}
