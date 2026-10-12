import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

// A Kotlin Multiplatform library for Android and iOS. The code lives in src/commonMain, platform code in
// src/androidMain and src/iosMain. Tests: src/commonTest runs on both platforms (testAndroidHostTest on the JVM,
// iosSimulatorArm64Test on a Mac), src/androidHostTest only on the JVM, e.g. with Robolectric.
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.plugins.kotlin.multiplatform.get().pluginId)
            // AGP 9 doesn't allow Kotlin Multiplatform with com.android.library; this is its plugin for KMP.
            pluginManager.apply(libs.plugins.android.kotlin.multiplatform.library.get().pluginId)
            pluginManager.apply(libs.plugins.dependency.analysis.get().pluginId)
            // The KMP Android plugin lints only the host tests; this one lints the main code too, commonMain included
            // (lintAndroidMain).
            pluginManager.apply("com.android.lint")

            extensions.configure<KotlinMultiplatformExtension> {
                // Room's database constructor (:crossfeature:data) is an expect object; such classes are still Beta
                // and warn without this, which fails CI.
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
                iosArm64()
                iosSimulatorArm64()
                // When a binary (the app's framework) calls a function its klibs lack, e.g. under another signature,
                // Kotlin/Native links it anyway, into a call that throws at run time. This fails the link instead:
                // a compiler bug once made the iOS app crash at start this way, with nothing in the build log.
                targets.withType(KotlinNativeTarget::class.java).configureEach {
                    binaries.all { freeCompilerArgs += "-Xpartial-linkage-loglevel=ERROR" }
                }
                targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
                    namespace = defaultNamespace()
                    compileSdk = AndroidConfig.COMPILE_SDK
                    minSdk = AndroidConfig.MIN_SDK
                    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(AndroidConfig.JAVA_VERSION.toString()))
                    // Host tests are off by default in this plugin. Robolectric runs them on the targetSdk of the
                    // module's merged manifest, which a library doesn't have, so it gets the app's; without the
                    // resources Robolectric sees no manifest and takes its oldest SDK, older than the app's minSdk.
                    withHostTest {
                        isIncludeAndroidResources = true
                        targetSdk { version = release(AndroidConfig.TARGET_SDK) }
                    }
                    // The lint settings of lintAndroidMain live here, not in the project's lint block.
                    lint.warningsAsErrors = warningsAsErrors
                }
            }
            tasks.withType<Test>().configureEach { configureHostTest() }
            configureKotlinWarnings()
            configureTestLogging()
        }
    }
}

// The namespace and package a library module gets from its path: :core:compose-utils -> ...core.composeutils.
internal fun Project.defaultNamespace(): String =
    "krio.systemdesign.shoppingapp" + path.replace(":", ".").replace("-", "")
