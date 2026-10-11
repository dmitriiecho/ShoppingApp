import com.jraska.module.graph.assertion.GraphRulesExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// The module rules from CLAUDE.md ("Modules"), checked by ./gradlew assertModuleGraph (CI runs it too).
// Only the dependencies listed here are allowed: any other one fails the check, so a new kind of dependency
// between modules is a decision made here. Each rule is one regex over the whole ":from -> :to" text.
// Applied to the apps: the check walks the graph of every app.
internal fun Project.configureModuleGraphRules() {
    pluginManager.apply(libs.plugins.module.graph.assertion.get().pluginId)

    extensions.configure<GraphRulesExtension> {
        // KMP modules declare their dependencies per source set; without these the check silently skips them.
        configurations += setOf(
            "commonMainApi",
            "commonMainImplementation",
            "androidMainApi",
            "androidMainImplementation",
            "iosMainApi",
            "iosMainImplementation",
        )
        allowed = arrayOf(
            // core knows nothing about the shop.
            """:core:.* -> :core:.*""",
            // crossfeature builds on core; within crossfeature only data uses the domain (ui takes plain values).
            """:crossfeature:(domain|ui|analytics) -> :core:.*""",
            """:crossfeature:data -> :(core:.*|crossfeature:domain)""",
            // A feature's ui uses only the design system and the shared components.
            """:feature:\w+:ui -> :(core:designsystem|crossfeature:ui)""",
            // A feature's impl: core, crossfeature without data, and its own ui, never another feature.
            """:feature:\w+:impl -> :(core:.*|crossfeature:(domain|ui|analytics))""",
            """:feature:(\w+):impl -> :feature:\1:ui""",
            // The UI kit shows the UI modules only.
            """:apps:uikit -> :(core:designsystem|crossfeature:ui|feature:\w+:ui)""",
            """:apps:shop -> :(core|crossfeature|feature):.*""",
        )
    }
}
