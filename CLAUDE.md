# ShoppingApp

Android app (Kotlin, Jetpack Compose, Hilt, Room, KSP). Gradle 9.8, AGP 9.4, JDK 21, compileSdk 37.
applicationId: `krio.systemdesign.shoppingapp`, launcher activity: `.MainActivity`.

## Modules
Folders are levels, bottom to top: `core/` → `shared/` → `feature/` → `apps/`. A module never depends on a level above its own.
- `core/` — knows nothing about the shop, would fit any app:
  - `:core:designsystem` — theme, icons, generic components (cards, buttons, fields, notices, placeholders, dialogs, screen states). Its strings are generic ("Close", "Retry").
    Every icon is `AppIcons.X`: Material Symbols as `ImageVector` in `core/designsystem/.../icons/symbols/`, one per file, named as on fonts.google.com/icons (`AppIcons.Search`, `AppIcons.ShoppingCartFilled`). There is no material-icons dependency: a new icon is an SVG from fonts.google.com/icons converted with the Valkyrie plugin, then listed in `apps/uikit/.../sections/designsystem/IconsSection.kt`.
  - `:core:compose-utils` — small helpers for screens that draw nothing themselves: `ObserveEffects`, `savedTextField`, `UiText`, shared element scopes. Only generic Compose/ViewModel/navigation glue goes here; anything visual goes to `:core:designsystem`, anything that knows the shop to `shared/`.
  - `:core:network`, `:core:config`
- `shared/` — shop code used by two or more features; `core` never depends on it. Code used by one feature stays in that feature.
  - `:shared:domain`, `:shared:data` — models, repositories, use cases shared by features; only `:apps:shop` depends on `:shared:data`.
  - `:shared:analytics` — analytics, pure Kotlin. Root: `Analytics` that features call, `AnalyticsEvent`, `analyticsParams`. `system/`: `AnalyticsSystem` (the systems the app uses) and `AnalyticsSystemClient` (code that sends to one system). `event/<topic>/`: events sent by two or more features.
    Each event is a class `XxxAnalyticsEvent` that lists its `systems`; it lives in the feature that sends it (`impl/.../analytics/`), or here when two or more features send it. `:apps:shop` (`analytics/`) holds one client class per `AnalyticsSystem`. Screen views are sent by `:apps:shop` (`navigation/ScreenViews.kt`); each feature names its screens in `NavDestination.xxxAnalyticsScreen()`, and a screen with no name isn't reported.
  - `:shared:ui` — shop components shared by features (`ProductCard`, `CartQuantityControl`, `OrderTotals`, `formatPrice`, ...), built from `:core:designsystem`. Takes plain values, never `:shared:domain` models.
- `feature/<name>/` — catalog, cart, promo, checkout, settings:
  - `:feature:<name>:impl` — screens, ViewModels, the feature's own use cases and navigation.
  - `:feature:<name>:ui` — shop components only this feature uses, on plain values; depends only on `:core:designsystem` and `:shared:ui`. Exists only when the feature has such components (not in settings).
- A library module's package mirrors its module path: `:shared:ui` → `...shoppingapp.shared.ui`, `:feature:cart:impl` → `...feature.cart.impl`. A hyphen in a module name is dropped in the package: `compose-utils` → `composeutils`.
- An app's package is its applicationId, so it stays when the app moves to another folder: `:apps:shop` → `krio.systemdesign.shoppingapp`, `:apps:uikit` → `...shoppingapp.uikit`.
- Where a styled component goes: would it fit any other app unchanged → `:core:designsystem`; knows shop words (product, cart, promo, price, stock) → `:shared:ui` if two or more features use it, else that feature's `ui` module. Features don't style Material components themselves.
- `apps/` — the applications; nothing depends on them:
  - `:apps:shop` — the shop app: entry point, navigation host.
  - `:apps:uikit` — separate app showing every styled component (UI kit catalog); applicationId `krio.systemdesign.shoppingapp.uikit`, launcher activity `.UiKitActivity`. Depends only on UI modules (`:core:designsystem`, `:shared:ui`, every `:feature:<name>:ui`), never on `impl`, domain or data.
    Its first screen has three groups: design system, shared components, feature components. Sections live in `apps/uikit/.../sections/{designsystem,shared,feature}/` and mirror the packages one to one: `core/designsystem/.../components/buttons` ↔ `ButtonsSection`, `shared/ui/.../product` ↔ `ProductSection`, `:feature:cart:ui` ↔ `CartFeatureSection`. A new component gets a sample in the section of its package. Design system samples use neutral texts, not the shop's.
- `build-logic/` (included build, not an app module) — convention plugins `shoppingapp.android.{library,application,compose,hilt,feature}`. compileSdk/minSdk/targetSdk/Java live in `build-logic/src/main/kotlin/AndroidConfig.kt`; a feature's `impl` module applies `libs.plugins.shoppingapp.android.feature`, a `ui` module applies `library` + `compose`; each declares only its own dependencies.
- `server/` — Ktor backend for the app (Kotlin 2.3, Ktor 3.6, data in JSON files). A separate Gradle build with its own wrapper and version catalog, not included in `settings.gradle.kts`. API, data rules and deploy: `server/README.md`.

## Build
- `./gradlew :apps:shop:assembleDebug` — build debug APK (`apps/shop/build/outputs/apk/debug/shop-debug.apk`)
- `./gradlew :apps:shop:installDebug` — build and install on the connected device
- `./gradlew :apps:uikit:installDebug` — build and install the UI kit catalog; in Android Studio it is the run configuration of the `uikit` module
- `cd server && ./gradlew test` — server tests; `server/deploy.sh` — test, build and restart the deployed server
- Never run two Gradle builds in the same checkout at once: they share `build/` dirs and corrupt each other's outputs.

Machine-specific setup (SDK paths, emulator access) lives in `CLAUDE.local.md`, if present.
