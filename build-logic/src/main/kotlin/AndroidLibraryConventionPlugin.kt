import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.HostTestBuilder
import com.android.build.api.variant.LibraryAndroidComponentsExtension
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
                // Robolectric runs unit tests on the targetSdk of the module's merged manifest; a library has none of
                // its own, so it gets the app's. Without the resources Robolectric sees no manifest and takes its
                // oldest SDK, older than the app's minSdk.
                testOptions.targetSdk = AndroidConfig.TARGET_SDK
                testOptions.unitTests.isIncludeAndroidResources = true
                testOptions.unitTests.all { it.configureHostTest() }
                lint.warningsAsErrors = warningsAsErrors
            }
            alignCoreKtx()
            configureKotlinWarnings()
            configureTestLogging()

            // Unit tests run on debug only: release would repeat the same tests, and `./gradlew test` would run
            // each one twice. KMP modules have a single variant and run theirs with testAndroidHostTest.
            extensions.configure<LibraryAndroidComponentsExtension> {
                beforeVariants(selector().withBuildType("release")) { variant ->
                    variant.hostTests[HostTestBuilder.UNIT_TEST_TYPE]?.enable = false
                }
            }

            // Kotlin LSP ("Kotlin by JetBrains") adds R.jar only to app modules, so R is unresolved in libraries.
            // The property is set only by its project import. builtBy declares where the jar comes from: the import
            // compiles release too, and Gradle fails a task that reads another task's output without a dependency.
            if (providers.systemProperty("com.jetbrains.ls.imports.gradle").isPresent) {
                dependencies {
                    add(
                        "compileOnly",
                        files(
                            layout.buildDirectory.file(
                                "intermediates/compile_r_class_jar/debug/generateDebugRFile/R.jar",
                            ),
                        ).builtBy("generateDebugRFile"),
                    )
                }
            }
        }
    }
}
