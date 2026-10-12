import com.android.build.api.dsl.CommonExtension
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.RoborazziExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

// Screenshot tests generated from a module's @Preview functions: Roborazzi draws each preview on Robolectric
// and compares it with the image saved in the module's screenshots/ folder. Apply after library (Android or KMP)
// and compose. The tests compare on every run (roborazzi.test.verify in gradle.properties): `test` in an Android
// module, `testAndroidHostTest` in a KMP one. recordRoborazziDebug / recordRoborazziAndroidHostTest save new
// images (docs/architecture/en/testing.md#screenshots).
class AndroidScreenshotsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.plugins.roborazzi.get().pluginId)

            if (pluginManager.hasPlugin(libs.plugins.android.kotlin.multiplatform.library.get().pluginId)) {
                // A KMP module: the previews are in commonMain, the tests run with the Android host tests.
                configureRoborazzi(packageToScan = provider { defaultNamespace() })
                tasks.withType<Test>().configureEach { drawLikeDevice() }
                // Roborazzi checks its dependencies by reading every configuration in afterEvaluate, and after that the
                // Android plugin no longer adds the dependencies of commonMain and androidMain to the host tests'
                // compile classpath: tests that use those libraries don't compile. Add them back here.
                configurations.named("androidHostTestCompileClasspath") {
                    listOf("commonMain", "androidMain").forEach { sourceSet ->
                        listOf("Api", "Implementation", "CompileOnly").forEach { kind ->
                            extendsFrom(configurations.getByName(sourceSet + kind))
                        }
                    }
                }
                extensions.configure<KotlinMultiplatformExtension> {
                    // The generated tests create the tester at run time, by its name. It is used only on Android, but is
                    // declared in commonTest, where the module's own tests declare it too (viewModelTest): the
                    // dependency analysis takes a commonTest declaration for unused when androidHostTest declares the
                    // same module.
                    sourceSets.getByName("commonTest").dependencies {
                        runtimeOnly(project(":core:compose-utils:testing"))
                    }
                    sourceSets.getByName("androidHostTest").dependencies {
                        implementation(libs.roborazzi.compose.preview.scanner.support)
                        implementation(libs.composable.preview.scanner)
                        implementation(libs.robolectric)
                        implementation(libs.junit)
                        implementation(project.dependencies.platform(libs.androidx.compose.bom))
                        implementation(libs.androidx.compose.ui.test.junit4)
                        runtimeOnly(libs.androidx.compose.ui.test.manifest)
                    }
                }
                return
            }

            val android = extensions.getByType(CommonExtension::class.java)
            // The module's own previews; the namespace is set in its build file, after this plugin.
            configureRoborazzi(packageToScan = provider { checkNotNull(android.namespace) })
            android.testOptions.unitTests.all { it.drawLikeDevice() }

            dependencies {
                "testImplementation"(libs.roborazzi.compose.preview.scanner.support)
                "testImplementation"(libs.composable.preview.scanner)
                // The generated tests create the tester at run time, by its name.
                "testRuntimeOnly"(project(":core:compose-utils:testing"))
                "testImplementation"(libs.robolectric)
                "testImplementation"(libs.junit)
                "testImplementation"(platform(libs.androidx.compose.bom))
                "testImplementation"(libs.androidx.compose.ui.test.junit4)
                // The activity the tests draw previews in.
                "debugRuntimeOnly"(libs.androidx.compose.ui.test.manifest)
            }
        }
    }
}

private fun Project.configureRoborazzi(packageToScan: Provider<String>) {
    extensions.configure<RoborazziExtension> {
        outputDir.set(layout.projectDirectory.dir("screenshots"))
        @OptIn(ExperimentalRoborazziApi::class)
        generateComposePreviewRobolectricTests {
            enable.set(true)
            packages.set(packageToScan.map { listOf(it) })
            // Slack's Compose lint rules make previews private.
            includePrivatePreviews.set(true)
            // Roborazzi's tester with the clock stopped, so endless animations don't hang the tests
            // (:core:compose-utils:testing).
            testerQualifiedClassName.set("krio.systemdesign.shoppingapp.core.composeutils.PausedClockPreviewTester")
            // Hands packages and includePrivatePreviews above to that tester.
            useScanOptionParametersInTester.set(true)
        }
    }
}

// Draws the way a device's GPU does, with shadows and rounded clips.
private fun Test.drawLikeDevice() {
    systemProperty("robolectric.pixelCopyRenderMode", "hardware")
}
