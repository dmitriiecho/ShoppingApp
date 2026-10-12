# Modules

> **Variant `kmp`**: Android and iOS, Metro, Ktor
>
> This page in the other variants:
> - [`main`](https://github.com/dmitriiecho/ShoppingApp/blob/main/docs/architecture/en/modules.md): Android, Hilt, Retrofit
> - [`kmp-ready`](https://github.com/dmitriiecho/ShoppingApp/blob/kmp-ready/docs/architecture/en/modules.md): Android, Metro, Ktor

[Русская версия](../ru/modules.md) · [All pages](README.md)

The folders are levels, from the bottom up: `core/` → `crossfeature/` → `feature/` → `apps/`. A module never depends on a level above its own, and features never depend on each other.

&nbsp;

## Levels

Each level has its own modules and its own purpose:

| Level | Modules | What it holds |
|---|---|---|
| `core/` | `designsystem`, `compose-utils`, `network`, `config` | code that would fit any app: theme, icons, generic components, Compose helpers, the HTTP client |
| `crossfeature/` | `domain`, `data`, `ui`, `analytics` | shop code that two or more features use |
| `feature/` | `catalog`, `cart`, `promo`, `checkout`, `settings` | one feature each, split into `ui` and `impl` |
| `apps/` | `shop`, `uikit` | the applications; nothing depends on them |

The levels are folders, not just dependency rules: putting everything under `core/`, as many projects do, hides which code knows about the shop. There are also no catch-all modules like `:core:common` that every feature ends up editing: `crossfeature/` has four modules, each with a clear role.

The middle level is named `crossfeature`, not `shared`: in Kotlin Multiplatform "shared" means code shared between platforms, and the `kmp` branch has that kind of code too.

Two kinds of modules sit inside the levels:

- **`testing` modules** hold test helpers that the tests of other modules use too, next to the module they belong to: `:core:network:testing`, `:crossfeature:domain:testing`. Kotlin Multiplatform modules have no test fixtures, so the helpers get a module of their own; only tests depend on it ([Testing](testing.md#names-and-places)).
- **The shop app is split by platform**, as JetBrains recommends: `:apps:shop:app` holds what both platforms share (`App()`, navigation, the bottom bar, analytics, the base of the app's graph), the iOS entry point and graph in `iosMain`, and builds the framework that the Xcode project in `apps/shop/ios` links ([Build](build.md#ios-app)); `:apps:shop:android` is the Android entry point (`MainActivity`, `ShoppingApp`, the Android graph, manifest, resources).

Outside the levels is [`contract/`](../../../contract/): the API's DTOs, shared with the server. It's a separate build that both the app and the server include, so the server, also a separate build, can use it without the app. `:crossfeature:data` and the features' `impl` modules that call the server depend on it ([Data](data.md#the-contract-with-the-server)).

&nbsp;

## Where new code goes

Code stays in the feature that uses it. When a second feature needs it, it moves to `crossfeature/`:

- **Used by one feature** — stays in that feature.
- **Needed by a second feature** — moves to `crossfeature/`: models, repositories and use cases to `:crossfeature:domain` / `:crossfeature:data`, shop UI components (the product card, the cart quantity control) to `:crossfeature:ui`.
- **Knows nothing about the shop** — goes to `core/`.

Styled components follow the same rule:

| The component | Module |
|---|---|
| would fit any other app unchanged | `:core:designsystem` |
| refers to shop concepts (product, cart, promo, price, stock) and is used by two or more features | `:crossfeature:ui` |
| refers to shop concepts and is used by only one feature | `:feature:<name>:ui` |

Features don't style Material components themselves, and every styled component has a sample in the UI kit app (`:apps:uikit`).

&nbsp;

## A feature: `ui` + `impl`

Every feature consists of two modules. `impl` holds everything the feature does, and `ui` holds its UI components, which take plain values:

```kotlin
// feature/cart/impl/build.gradle.kts
plugins {
    alias(libs.plugins.shoppingapp.kmp.feature)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:cart:ui"))
            implementation(project(":crossfeature:domain"))
            implementation(project(":crossfeature:ui"))
            implementation(project(":crossfeature:analytics"))
            implementation(project(":core:designsystem"))
            // ...
        }
    }
}
```

- **`ui` is a separate module so the UI kit can show it** without seeing screens, ViewModels or data. A feature without such components has no `ui` module (for example, `settings`).
- **There is no `api` module.** Features never depend on each other, so only the app needs a feature's API, and it connects the features through callbacks (see [Navigation](navigation.md)).
- **Only the apps' graphs use `:crossfeature:data`**: `:apps:shop:android` and the iOS part of `:apps:shop:app`. Features see repository interfaces from `:crossfeature:domain`; the app's dependency graph (Metro) provides the implementations.
- **The module of a platform's graph depends directly on every module with DI contributions** (`@ContributesTo`, `@ContributesBinding` and the like) or with `@Inject` classes the graph creates, though its own code doesn't use them: `:apps:shop:android` lists the features, `:crossfeature:data`, `:core:network` and others. Metro builds the graph when it compiles that module and sees only what is on its compile classpath; for the same reason a module's types used in its bindings are `api` (Room and DataStore in `:crossfeature:data`).

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
| `:crossfeature:ui` | `krio.systemdesign.shoppingapp.crossfeature.ui` |
| `:feature:cart:impl` | `krio.systemdesign.shoppingapp.feature.cart.impl` |
| `:core:compose-utils` | `krio.systemdesign.shoppingapp.core.composeutils` (the hyphen is dropped) |
| `:apps:shop:android` | `krio.systemdesign.shoppingapp` |
| `:apps:shop:app` | `krio.systemdesign.shoppingapp`: part of the app, so it has the app's package; its Android namespace still follows its path, since a library can't share the app's |

Because an app's package is tied to its applicationId, it doesn't change when the app moves to another folder.

&nbsp;

## The check

`./gradlew assertModuleGraph` checks every dependency between modules against an allowlist in [`ModuleGraphRules.kt`](../../../build-logic/src/main/kotlin/ModuleGraphRules.kt), and CI runs it on every pull request. Each rule is a single regex matched against the "from -> to" string:

```kotlin
allowed = arrayOf(
    """:core:.* -> :core:.*""",
    """:crossfeature:(domain|ui|analytics) -> :core:.*""",
    """:crossfeature:data -> :(core:.*|crossfeature:domain)""",
    """:feature:\w+:ui -> :(core:designsystem|crossfeature:ui)""",
    """:feature:\w+:impl -> :(core:.*|crossfeature:(domain|ui|analytics))""",
    """:feature:(\w+):impl -> :feature:\1:ui""",   // only its own ui
    """:apps:uikit -> :(core:designsystem|crossfeature:ui|feature:\w+:ui)""",
    """:apps:shop:android -> :apps:shop:app""",
    """:apps:shop:(android|app) -> :(core|crossfeature|feature):.*""",
)
```

Any dependency that no rule allows fails the build:

```text
[':feature:catalog:impl' -> ':feature:cart:impl'] not allowed by any of [...]
```

Adding a new kind of dependency between modules is a deliberate decision: it needs a new rule in that file, and the rule shows up in the diff.
