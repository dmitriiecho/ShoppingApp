# ShoppingApp

A demo shop for Android. How it is built and why: [`docs/architecture/en/`](docs/architecture/en/README.md).

| | |
|---|---|
| Stack | Kotlin, Jetpack Compose, Hilt (KSP), Room |
| Build | Gradle 9.8, AGP 9.4, JDK 21, compileSdk 37 |
| Shop app | `:apps:shop`, applicationId `krio.systemdesign.shoppingapp`, launcher activity `.MainActivity` |
| UI kit app | `:apps:uikit`, applicationId `krio.systemdesign.shoppingapp.uikit`, launcher activity `.UiKitActivity` |

Machine-specific setup (SDK paths, emulator access) lives in `CLAUDE.local.md`, if present.

## Modules

Folders are levels, bottom to top: `core/` → `shared/` → `feature/` → `apps/`. A module never depends on a level above its own.

| Level | Module | Holds |
|---|---|---|
| `core/`: knows nothing about the shop | `:core:designsystem` | theme, icons, generic components (cards, buttons, fields, bars, notices, placeholders, dialogs, screen states); only generic strings ("Close", "Retry") |
| | `:core:compose-utils` | Compose/ViewModel/navigation glue that draws nothing: `ObserveEffects`, `savedTextField`, `UiText`, shared element scopes |
| | `:core:network`, `:core:config` | the HTTP client; the server and deep link addresses |
| `shared/`: shop code used by two or more features | `:shared:domain` | models, repository interfaces, use cases; pure Kotlin |
| | `:shared:data` | their implementations; only `:apps:shop` depends on it |
| | `:shared:ui` | shop components (`ProductCard`, `CartQuantityControl`, `OrderTotals`, `formatPrice`, ...) built from `:core:designsystem`; take plain values, never `:shared:domain` models |
| | `:shared:analytics` | analytics, pure Kotlin (see [Analytics](#analytics)) |
| `feature/<name>/`: catalog, cart, promo, checkout, settings | `:feature:<name>:impl` | screens, ViewModels, the feature's own use cases and navigation |
| | `:feature:<name>:ui` | shop components only this feature uses, on plain values; depends only on `:core:designsystem` and `:shared:ui`; exists only when the feature has such components (not in settings) |
| `apps/`: nothing depends on them | `:apps:shop` | the shop: entry point, navigation host |
| | `:apps:uikit` | a sample of every styled component (see [UI kit](#ui-kit)); depends only on UI modules, never on `impl`, domain or data |

- **The rules are checked** by `./gradlew assertModuleGraph` (a CI step too) against `build-logic/src/main/kotlin/ModuleGraphRules.kt`. A new kind of dependency between modules needs a rule there.
- **An `impl` module shows only what `:apps:shop` wires**, all in `presentation/navigation`: the routes graph, `graph()`, analytics screen names. Everything else is `internal`.

More: [`modules.md`](docs/architecture/en/modules.md).

## Where code goes

Code used by one feature stays in that feature; it moves to `shared/` when a second feature needs it. `:core:compose-utils` takes only generic glue: anything visual goes to `:core:designsystem`, anything that knows the shop to `shared/`.

A styled component goes by the same rule:

| The component | Module |
|---|---|
| would fit any other app unchanged | `:core:designsystem` |
| knows shop words (product, cart, promo, price, stock), two or more features use it | `:shared:ui` |
| knows shop words, one feature uses it | `:feature:<name>:ui` |

Features don't style Material components themselves.

A package follows the module:

| Module | Package |
|---|---|
| library | mirrors its module path: `:feature:cart:impl` → `krio.systemdesign.shoppingapp.feature.cart.impl`; a hyphen is dropped: `compose-utils` → `composeutils` |
| app | its applicationId, so it stays when the app moves to another folder: `:apps:shop` → `krio.systemdesign.shoppingapp` |

## Conventions

### Icons

- Every icon is `AppIcons.X`: a Material Symbol as `ImageVector` in `core/designsystem/.../icons/symbols/`, one per file, named as on fonts.google.com/icons (`AppIcons.Search`, `AppIcons.ShoppingCartFilled`).
- There is no material-icons dependency. A new icon is an SVG from fonts.google.com/icons, converted with the Valkyrie plugin, then listed in `apps/uikit/.../sections/designsystem/IconsSection.kt`.

### Analytics

- **`:shared:analytics`**: in the root `Analytics` (what features call), `AnalyticsEvent`, `analyticsParams`; in `system/` `AnalyticsSystem` (the systems the app uses) and `AnalyticsSystemClient` (code that sends to one system); in `event/<topic>/` events sent by two or more features.
- **An event** is a class `XxxAnalyticsEvent` that lists its `systems`. It lives in the feature that sends it (`impl/.../analytics/`), or in `:shared:analytics` when two or more features send it.
- **Clients**: `:apps:shop` (`analytics/`) holds one client class per `AnalyticsSystem`.
- **Screen views** are sent by `:apps:shop` (`navigation/ScreenViews.kt`). Each feature names its screens in `NavDestination.xxxAnalyticsScreen()`; a screen with no name isn't reported.

### UI kit

- Its first screen has three groups: design system, shared components, feature components.
- Sections live in `apps/uikit/.../sections/{designsystem,shared,feature}/` and mirror the packages one to one: `core/designsystem/.../components/buttons` ↔ `ButtonsSection`, `shared/ui/.../product` ↔ `ProductSection`, `:feature:cart:ui` ↔ `CartFeatureSection`.
- A new component gets a sample in the section of its package. Design system samples use neutral texts, not the shop's.

### Tests

- Tests follow [`testing.md`](docs/architecture/en/testing.md).
- A test helper (`TestXxx` stand-in, `testXxx()` builder, `xxxTest {}` entry function) used by tests of two or more modules lives in the test fixtures of the module that owns what it replaces (`:shared:analytics` → `TestAnalyticsClient`). One used by a single module stays in its `src/test`.
- The prefix `Fake` is taken by real code: the analytics stand-ins in `:apps:shop`.

### build-logic

`build-logic/` is an included build with the convention plugins `shoppingapp.android.{library,application,compose,hilt,feature,screenshots}` and `shoppingapp.jvm.library`. compileSdk, minSdk, targetSdk and Java live in `build-logic/src/main/kotlin/AndroidConfig.kt`. Each module declares only its own dependencies.

| Module | Applies |
|---|---|
| `:feature:<name>:impl` | `libs.plugins.shoppingapp.android.feature` |
| `:feature:<name>:ui` | `library` + `compose` + `screenshots` |
| pure Kotlin (`:shared:domain`, `:shared:analytics`, `:core:config`) | `jvm.library` |

A library module with `@Preview`s applies `screenshots`: screenshot tests made from the previews. `:apps:uikit` has none of its own.

## Server

`server/` is the Ktor backend for the app (Kotlin 2.3, Ktor 3.6, data in JSON files): a separate Gradle build with its own wrapper and version catalog, not included in `settings.gradle.kts`. API, data rules and deploy: [`server/README.md`](server/README.md).

## Build

The main commands, from the project root:

| Command | Does |
|---|---|
| `./gradlew :apps:shop:assembleDebug` | builds the debug APK: `apps/shop/build/outputs/apk/debug/shop-debug.apk` |
| `./gradlew :apps:shop:installDebug` | builds and installs it on the connected device |
| `./gradlew :apps:uikit:installDebug` | builds and installs the UI kit; in Android Studio, the run configuration of the `uikit` module |
| `./gradlew test` | every app test, once (release unit tests are off); screenshots compared with `<module>/screenshots/` |
| `cd server && ./gradlew test` | server tests |
| `server/deploy.sh` | tests, builds and restarts the deployed server |

The CI checks locally (CI treats Kotlin and lint warnings as errors):

```sh
./gradlew -PwarningsAsErrors=true spotlessCheck assertModuleGraph buildHealth lintDebug test :apps:shop:assembleDebug :apps:uikit:assembleDebug
cd server && ./gradlew -PwarningsAsErrors=true spotlessCheck test
```

> [!WARNING]
> Never run two Gradle builds in the same checkout at once: they share `build/` dirs and corrupt each other's outputs.

### Screenshots after a UI change

1. Check that only the screens meant to change did, either way:
   - before recording: `./gradlew test` fails only those previews and leaves `build/outputs/roborazzi/*_compare.png`;
   - after recording: `git status` shows only their images.
2. Record: `./gradlew recordRoborazziDebug -Proborazzi.cleanupOldScreenshots=true` saves the new screenshots and deletes those of removed previews; in Android Studio, the Record screenshots run configuration.
3. Commit the images with the change.

- **Don't record when a change wasn't meant to alter the UI**: a failed screenshot test is then a real problem.
- **Never put the cleanup flag in `gradle.properties`**: a filtered test run would then delete the module's other screenshots.
