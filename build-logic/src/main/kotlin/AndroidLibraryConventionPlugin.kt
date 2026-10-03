import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

// Android-библиотека проекта с общими compileSdk, minSdk и версией Java.
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.plugins.android.library.get().pluginId)

            extensions.configure<LibraryExtension> {
                configureAndroid(this)
            }

            // Kotlin LSP (расширение «Kotlin by JetBrains») подключает R.jar только модулям-приложениям,
            // поэтому в библиотеках R подчёркнут как неизвестный. Это свойство задаёт только его импорт проекта:
            // обычная сборка его не видит. R.jar появляется после первой сборки модуля.
            if (providers.systemProperty("com.jetbrains.ls.imports.gradle").isPresent) {
                dependencies {
                    add(
                        "compileOnly",
                        files(layout.buildDirectory.file("intermediates/compile_r_class_jar/debug/generateDebugRFile/R.jar")),
                    )
                }
            }
        }
    }
}
