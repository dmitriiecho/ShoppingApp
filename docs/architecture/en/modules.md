# Modules

[Русская версия](../ru/modules.md) · [All pages](README.md)

The folders are levels, from the bottom up: `core/` → `shared/` → `feature/` → `apps/`. A module never depends on a level above its own, and features never depend on each other.

&nbsp;

## Levels

Each level has its own modules and its own purpose:

| Level | Modules | What it holds |
|---|---|---|
| `core/` | `designsystem`, `compose-utils`, `network`, `config` | code that would fit any app: theme, icons, generic components, Compose helpers, the HTTP client |
| `shared/` | `domain`, `data`, `ui`, `analytics` | shop code that two or more features use |
| `feature/` | `catalog`, `cart`, `promo`, `checkout`, `settings` | one feature each, split into `ui` and `impl` |
| `apps/` | `shop`, `uikit` | the applications; nothing depends on them |

The levels are folders, not just dependency rules: putting everything under `core/`, as many projects do, hides which code knows about the shop. There are also no catch-all modules like `:core:common` that every feature ends up editing: `shared/` has four modules, each with a clear role.

&nbsp;

## Where new code goes

Code stays in the feature that uses it. When a second feature needs it, it moves to `shared/`:

- **Used by one feature** — stays in that feature.
- **Needed by a second feature** — moves to `shared/`: models, repositories and use cases to `:shared:domain` / `:shared:data`, shop UI components (the product card, the cart quantity control) to `:shared:ui`.
- **Knows nothing about the shop** — goes to `core/`.

Styled components follow the same rule:

| The component | Module |
|---|---|
| would fit any other app unchanged | `:core:designsystem` |
| refers to shop concepts (product, cart, promo, price, stock) and is used by two or more features | `:shared:ui` |
| refers to shop concepts and is used by only one feature | `:feature:<name>:ui` |

Features don't style Material components themselves, and every styled component has a sample in the UI kit app (`:apps:uikit`).

&nbsp;

## A feature: `ui` + `impl`

Every feature consists of two modules. `impl` holds everything the feature does, and `ui` holds its UI components, which take plain values:

```kotlin
// feature/cart/impl/build.gradle.kts
plugins {
    alias(libs.plugins.shoppingapp.android.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

dependencies {
    implementation(project(":feature:cart:ui"))
    implementation(project(":shared:domain"))
    implementation(project(":shared:ui"))
    implementation(project(":shared:analytics"))
    implementation(project(":core:designsystem"))
    // ...
}
```

- **`ui` is a separate module so the UI kit can show it** without seeing screens, ViewModels or data. A feature without such components has no `ui` module (for example, `settings`).
- **There is no `api` module.** Features never depend on each other, so only the app needs a feature's API, and it connects the features through callbacks (see [Navigation](navigation.md)).
- **Only `:apps:shop` depends on `:shared:data`.** Features see repository interfaces from `:shared:domain`; the app's dependency graph (Metro) provides the implementations.
- **`:apps:shop` depends directly on every module with DI contributions** (`@ContributesTo`, `@ContributesBinding` and the like), `:core:network` included, though the app's code doesn't use it. Metro builds the graph when it compiles `:apps:shop` and sees only the contributions on its compile classpath; for the same reason a module's types used in its bindings are `api` (Room and DataStore in `:shared:data`).

&nbsp;

## What a feature exposes

In an `impl` module, only what the app wires up is public, and all of it lives in `presentation/navigation` (plus the screen names for analytics, see [Analytics](analytics.md)). Everything else — screens, ViewModels, UI state, use cases, repositories — is `internal`:

```kotlin
object CatalogRoutes {
    @Serializable data object Graph                          // the app opens the feature
    @Serializable internal data object ProductList
    @Serializable internal data class ProductDetails(/* ... */) : ProductDetailsRoute
}

fun CatalogNavigationScope.graph(navController: NavController, onClose: () -> Unit) { /* ... */ }
```

&nbsp;

## Packages

A library's package mirrors its module path, and an app's package is its applicationId:

| Module | Package |
|---|---|
| `:shared:ui` | `krio.systemdesign.shoppingapp.shared.ui` |
| `:feature:cart:impl` | `krio.systemdesign.shoppingapp.feature.cart.impl` |
| `:core:compose-utils` | `krio.systemdesign.shoppingapp.core.composeutils` (the hyphen is dropped) |
| `:apps:shop` | `krio.systemdesign.shoppingapp` |

Because an app's package is tied to its applicationId, it doesn't change when the app moves to another folder.

&nbsp;

## The check

`./gradlew assertModuleGraph` checks every dependency between modules against an allowlist in [`ModuleGraphRules.kt`](../../../build-logic/src/main/kotlin/ModuleGraphRules.kt), and CI runs it on every pull request. Each rule is a single regex matched against the "from -> to" string:

```kotlin
allowed = arrayOf(
    """:core:.* -> :core:.*""",
    """:shared:(domain|ui|analytics) -> :core:.*""",
    """:shared:data -> :(core:.*|shared:domain)""",
    """:feature:\w+:ui -> :(core:designsystem|shared:ui)""",
    """:feature:\w+:impl -> :(core:.*|shared:(domain|ui|analytics))""",
    """:feature:(\w+):impl -> :feature:\1:ui""",   // only its own ui
    """:apps:uikit -> :(core:designsystem|shared:ui|feature:\w+:ui)""",
    """:apps:shop -> :(core|shared|feature):.*""",
)
```

Any dependency that no rule allows fails the build:

```text
[':feature:catalog:impl' -> ':feature:cart:impl'] not allowed by any of [...]
```

Adding a new kind of dependency between modules is a deliberate decision: it needs a new rule in that file, and the rule shows up in the diff.
