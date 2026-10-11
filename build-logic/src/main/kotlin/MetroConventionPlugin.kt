import dev.zacsweers.metro.gradle.DiagnosticSeverity
import dev.zacsweers.metro.gradle.MetroPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// Metro, compile-time DI. Apply after the Android or Kotlin JVM plugin.
class MetroConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.plugins.metro.get().pluginId)

            extensions.configure<MetroPluginExtension> {
                // Metro obeys Kotlin's internal: a contributed internal class is hidden from the app's graph in
                // another module. With this, Metro generates a public provider for it and the class stays internal.
                generateContributionProviders.set(true)
                // A non-public @ContributesTo binding container is skipped by the app's graph without a word:
                // fail the build instead of a puzzling "No binding found" later.
                nonPublicContributionSeverity.set(DiagnosticSeverity.ERROR)
            }
        }
    }
}
