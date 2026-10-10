# Modules

[Русская версия](../ru/modules.md) · [All pages](README.md)

The folders are levels, from the bottom up: `core/` → `shared/` → `feature/` → `apps/`. A module depends on modules of lower levels, and within its own level only in three cases: `core` modules on each other, `shared:data` on `shared:domain`, and a feature's `impl` on its own `ui`. Features never depend on each other, and neither do the apps. So the level of any code is seen right from its path.

&nbsp;

## Levels

Each level has its own modules and its own meaning:

| Level | Modules | What it holds |
|---|---|---|
| `core/` | `designsystem`, `compose-utils`, `network`, `config` | code that would fit any app: theme, icons, generic components, Compose helpers, the HTTP client |
| `shared/` | `domain`, `data`, `ui`, `analytics` | shop code that two or more features use |
| `feature/` | `catalog`, `cart`, `promo`, `checkout`, `settings` | one feature each, split into `ui` and `impl` |
| `apps/` | `shop`, `uikit` | the applications; nothing depends on them |

The levels are folders, not only dependencies: keeping everything under `core/`, as many projects do, hides which code knows about the shop. And there are no catch-all modules such as `:core:common` that every feature edits: `shared/` has four modules with strict roles.

&nbsp;

## Where new code goes

Code stays in the feature that uses it. When a second feature needs it, it is moved to `shared/`:

- **Used by one feature** — stays in that feature.
- **Needed by a second feature** — moves to `shared/`: models, repositories and use cases to `:shared:domain` / `:shared:data`, shop UI components (the product card, the cart quantity control) to `:shared:ui`.
- **Knows nothing about the shop** — goes to `core/`.

A styled component goes by the same rule:

| The component | Module |
|---|---|
| would fit any other app unchanged | `:core:designsystem` |
| knows shop words (product, cart, promo, price, stock) and two or more features use it | `:shared:ui` |
| knows shop words and one feature uses it | `:feature:<name>:ui` |

Features don't style Material components themselves, and every styled component has a sample in the UI kit app (`:apps:uikit`).

&nbsp;

## A feature: `ui` + `impl`

Every feature is two modules. `impl` holds everything the feature does, and `ui` holds its UI components on plain values:

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

- **`ui` is separate so the UI kit can show it** without seeing screens, ViewModels or data. A feature without such components has no `ui` module (`settings`).
- **There is no `api` module.** Features never depend on each other, so only the app needs a feature's API, and it joins the features with callbacks (see [Navigation](navigation.md)).
- **Only `:apps:shop` depends on `:shared:data`.** Features see repository interfaces from `:shared:domain`; Hilt in the app provides the implementations.

&nbsp;

## What a feature shows outside

In an `impl` module only what the app wires is public, all in `presentation/navigation`. Everything else — screens, ViewModels, UI state, use cases, repositories — is `internal`:

```kotlin
object CatalogRoutes {
    @Serializable data object Graph                          // the app opens the feature
    @Serializable internal data object ProductList
    @Serializable internal data class ProductDetails(/* ... */) : ProductDetailsRoute
}

fun CatalogNavigationScope.graph(navController: NavController, onClose: () -> Unit) { /* ... */ }

fun NavDestination.catalogAnalyticsScreen(): AnalyticsScreen? = /* ... */
```

The app needs one screen directly: it shows the catalog's product details inside the cart tab through the inline function `productDetailsScreen<T>()`. An inline function is copied into the app's code, so the screen is marked like this:

```kotlin
@Composable
@PublishedApi
internal fun ProductDetailsScreen(
    onBack: () -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
)
```

The copied code of that function can call it; the app can't call it by hand.

&nbsp;

## Packages

A library's package mirrors its module path, and an app's package is its applicationId:

| Module | Package |
|---|---|
| `:shared:ui` | `krio.systemdesign.shoppingapp.shared.ui` |
| `:feature:cart:impl` | `krio.systemdesign.shoppingapp.feature.cart.impl` |
| `:core:compose-utils` | `krio.systemdesign.shoppingapp.core.composeutils` (the hyphen is dropped) |
| `:apps:shop` | `krio.systemdesign.shoppingapp` |

An app's package is tied to its applicationId, so it doesn't change when the app moves to another folder.

&nbsp;

## The check

`./gradlew assertModuleGraph` checks every dependency between modules against an allowlist in [`ModuleGraphRules.kt`](../../../build-logic/src/main/kotlin/ModuleGraphRules.kt), and CI runs it on every pull request. Each rule is one regex over the "from → to" text:

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

A dependency no rule allows fails the build:

```text
[':feature:catalog:impl' -> ':feature:cart:impl'] not allowed by any of [...]
```

A new kind of dependency between modules is a decision: it needs a new rule in that file, which shows up in the diff.
