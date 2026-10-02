import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// Модуль приложения: те же настройки, что у библиотек, плюс targetSdk.
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")

            extensions.configure<ApplicationExtension> {
                configureAndroid(this)
                defaultConfig.targetSdk = AndroidConfig.TARGET_SDK

                buildTypes.getByName("release") {
                    // Приложения проекта — демонстрационные стенды: release подписан отладочным ключом,
                    // чтобы его можно было поставить на эмулятор. Для публикации в магазин понадобится свой ключ.
                    signingConfig = signingConfigs.getByName("debug")
                    optimization {
                        enable = false
                    }
                }
            }
        }
    }
}
