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

                // The debug key is kept in the repo, so a build from any computer has the same signature.
                // Deep links depend on it: Android opens them in the app only if the key's SHA-256 is listed in
                // https://dmitriiecho.github.io/.well-known/assetlinks.json.
                signingConfigs.getByName("debug") {
                    storeFile = rootProject.file("build-logic/debug.keystore")
                    storePassword = "android"
                    keyAlias = "androiddebugkey"
                    keyPassword = "android"
                }

                buildTypes.getByName("release") {
                    // Demo apps: release is signed with the debug key so it installs on an emulator.
                    // Publishing to a store would need a real key.
                    signingConfig = signingConfigs.getByName("debug")
                    // R8: shrinks code and resources. The libraries bring their own keep rules; the app's own go
                    // to src/main/keepRules/*.keep.
                    optimization {
                        enable = true
                    }
                }
            }
        }
    }
}
