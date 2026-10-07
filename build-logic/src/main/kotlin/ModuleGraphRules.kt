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
        allowed = arrayOf(
            // core knows nothing about the shop.
            """:core:.* -> :core:.*""",
            // shared builds on core; within shared only data uses the domain (ui takes plain values).
            """:shared:(domain|ui|analytics) -> :core:.*""",
            """:shared:data -> :(core:.*|shared:domain)""",
            // A feature's ui uses only the design system and the shared components.
            """:feature:\w+:ui -> :(core:designsystem|shared:ui)""",
            // A feature's impl: core, shared without data, and its own ui, never another feature.
            """:feature:\w+:impl -> :(core:.*|shared:(domain|ui|analytics))""",
            """:feature:(\w+):impl -> :feature:\1:ui""",
            // The UI kit shows the UI modules only.
            """:apps:uikit -> :(core:designsystem|shared:ui|feature:\w+:ui)""",
            """:apps:shop -> :(core|shared|feature):.*""",
        )
    }
}
