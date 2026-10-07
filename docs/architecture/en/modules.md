# Modules

[Русская версия](../ru/modules.md) · [All pages](README.md)

## Levels

The folders are levels, from the bottom up. A module depends only on modules of its own level or below, so the level of any code is visible from its path.

| Level | Modules | What it holds |
|---|---|---|
| `core/` | `designsystem`, `compose-utils`, `network`, `config` | code that would fit any app: theme, icons, generic components, Compose helpers, the HTTP client |
| `shared/` | `domain`, `data`, `ui`, `analytics` | shop code that two or more features use |
| `feature/` | `catalog`, `cart`, `promo`, `checkout`, `settings` | one feature each, split into `ui` and `impl` |
| `apps/` | `shop`, `uikit` | the applications; nothing depends on them |

### Why levels in folders

- **The level is seen without opening a build file.** Keeping everything under `core/`, as many projects do, hides which code knows about the shop.
- **No catch-all modules.** There is no `:core:common` or `:core:error` that every feature edits. `shared/` has four modules with strict roles.

## Where new code goes

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

## A feature: `ui` + `impl`

| Module | Holds | Depends on |
|---|---|---|
| `:feature:<name>:impl` | screens, ViewModels, the feature's own use cases, repositories and navigation | `core/`, `:shared:domain`, `:shared:ui`, `:shared:analytics`, its own `ui` |
| `:feature:<name>:ui` | shop UI components only this feature uses, on plain values | `:core:designsystem`, `:shared:ui` |

- **`ui` is separate so the UI kit can show it** without seeing screens, ViewModels or data. A feature without such components has no `ui` module (`settings`).
- **There is no `api` module.** Features never depend on each other, so nobody but the app needs a feature's API. The app joins them with callbacks (see [Navigation](navigation.md)).
- **Only `:apps:shop` depends on `:shared:data`.** Features see repository interfaces from `:shared:domain`; Hilt in the app provides the implementations.

## What a feature shows outside

In an `impl` module only what the app wires is public, all in `presentation/navigation`:

| Public | Why the app needs it |
|---|---|
| `XxxRoutes.Graph` | to open the feature |
| `NavGraphBuilder.xxx`, `XxxNavigationScope.graph(...)` | to add the feature's graph |
| `NavDestination.xxxAnalyticsScreen()` | to name screens for analytics |
| `ProductDetailsRoute`, `productDetailsScreen<T>()` (catalog only) | to show product details inside the cart tab |

Everything else — screens, ViewModels, UI state, use cases, repositories — is `internal`. `ProductDetailsScreen` is `@PublishedApi internal`: the public inline `productDetailsScreen<T>()` calls it from the app's code, but nobody can call it by hand.

## Packages

- **A library's package mirrors its module path:** `:shared:ui` → `krio.systemdesign.shoppingapp.shared.ui`, `:feature:cart:impl` → `…feature.cart.impl`. A hyphen is dropped: `compose-utils` → `composeutils`.
- **An app's package is its applicationId** (`:apps:shop` → `krio.systemdesign.shoppingapp`), so it doesn't change when the app moves to another folder.

## The check

`./gradlew assertModuleGraph` checks every dependency between modules against an allowlist in [`ModuleGraphRules.kt`](../../../build-logic/src/main/kotlin/ModuleGraphRules.kt), and CI runs it on every pull request. A dependency no rule allows fails the build:

```text
[':feature:catalog:impl' -> ':feature:cart:impl'] not allowed by any of [...]
```

A new kind of dependency between modules is a decision: it needs a new rule there, which shows up in the diff.
