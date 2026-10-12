# ShoppingApp

A demo shop for Android and iOS (Kotlin Multiplatform, moving there step by step on this branch). How it is built and why: [`docs/architecture/en/`](docs/architecture/en/README.md).

| | |
|---|---|
| Stack | Kotlin Multiplatform, Jetpack Compose and Compose Multiplatform, Metro, Room |
| Build | Gradle 9.8, AGP 9.4, JDK 21, compileSdk 37 |
| Shop app | `:apps:shop:android` (starts the shared `:apps:shop:app`), applicationId `krio.systemdesign.shoppingapp`, launcher activity `.MainActivity` |
| UI kit app | `:apps:uikit`, applicationId `krio.systemdesign.shoppingapp.uikit`, launcher activity `.UiKitActivity` |
| iOS app | Xcode project `apps/shop/ios`, bundle id `krio.systemdesign.shoppingapp`; links the `ShopApp` framework of `:apps:shop:app`; builds only on a Mac |

Machine-specific setup (SDK paths, emulator access) lives in `CLAUDE.local.md`, if present.

## Modules

Folders are levels, bottom to top: `core/` → `crossfeature/` → `feature/` → `apps/`. A module never depends on a level above its own.

| Level | Module | Holds |
|---|---|---|
| `core/`: knows nothing about the shop | `:core:designsystem` | theme, icons, generic components (cards, buttons, fields, bars, notices, placeholders, dialogs, screen states); only generic strings ("Close", "Retry") |
| | `:core:compose-utils` | Compose/ViewModel/navigation glue that draws nothing: `ObserveEffects`, `savedTextField`, `UiText`, shared element scopes, `injectedViewModel` |
| | `:core:network`, `:core:config` | the HTTP client; the server and deep link addresses |
| `crossfeature/`: shop code used by two or more features | `:crossfeature:domain` | models, repository interfaces, use cases; pure Kotlin |
| | `:crossfeature:data` | their implementations; only the apps' graphs use it (`:apps:shop:android`, the iOS part of `:apps:shop:app`) |
| | `:crossfeature:ui` | shop components (`ProductCard`, `CartQuantityControl`, `OrderTotals`, `formatPrice`, ...) built from `:core:designsystem`; take plain values, never `:crossfeature:domain` models |
| | `:crossfeature:analytics` | analytics, pure Kotlin (see [Analytics](#analytics)) |
| `feature/<name>/`: catalog, cart, promo, checkout, settings | `:feature:<name>:impl` | screens, ViewModels, the feature's own use cases and navigation |
| | `:feature:<name>:ui` | shop components only this feature uses, on plain values; depends only on `:core:designsystem` and `:crossfeature:ui`; exists only when the feature has such components (not in settings) |
| `apps/`: nothing depends on them | `:apps:shop:app` | the shop's code for both platforms: `App()`, the navigation host, the bottom bar, analytics, the base of the app's graph; in `iosMain` the iOS entry point and graph (`IosAppGraph`); builds the `ShopApp` framework |
| | `:apps:shop:android` | the Android app: `MainActivity`, `ShoppingApp`, the graph for Android, manifest and resources |
| | `:apps:uikit` | a sample of every styled component (see [UI kit](#ui-kit)); depends only on UI modules, never on `impl`, domain or data |

- **The rules are checked** by `./gradlew assertModuleGraph` (a CI step too) against `build-logic/src/main/kotlin/ModuleGraphRules.kt`. A new kind of dependency between modules needs a rule there.
- **An `impl` module shows only what `:apps:shop:app` wires**, all in `presentation/navigation`: the routes graph, `graph()`, analytics screen names. Everything else is `internal`.

More: [`modules.md`](docs/architecture/en/modules.md).

## Where code goes

Code used by one feature stays in that feature; it moves to `crossfeature/` when a second feature needs it. `:core:compose-utils` takes only generic glue: anything visual goes to `:core:designsystem`, anything that knows the shop to `crossfeature/`.

A styled component goes by the same rule:

| The component | Module |
|---|---|
| would fit any other app unchanged | `:core:designsystem` |
| knows shop words (product, cart, promo, price, stock), two or more features use it | `:crossfeature:ui` |
| knows shop words, one feature uses it | `:feature:<name>:ui` |

Features don't style Material components themselves.

A package follows the module:

| Module | Package |
|---|---|
| library | mirrors its module path: `:feature:cart:impl` → `krio.systemdesign.shoppingapp.feature.cart.impl`; a hyphen is dropped: `compose-utils` → `composeutils` |
| app, and every module of one app | its applicationId, so it stays when the app moves to another folder: `:apps:shop:android`, `:apps:shop:app` → `krio.systemdesign.shoppingapp` (a library's Android namespace still follows its path) |

## Conventions

### Icons

- Every icon is `AppIcons.X`: a Material Symbol as `ImageVector` in `core/designsystem/.../icons/symbols/`, one per file, named as on fonts.google.com/icons (`AppIcons.Search`, `AppIcons.ShoppingCartFilled`).
- There is no material-icons dependency. A new icon is an SVG from fonts.google.com/icons, converted with the Valkyrie plugin, then listed in `apps/uikit/.../sections/designsystem/IconsSection.kt`.

### Analytics

- **`:crossfeature:analytics`**: in the root `Analytics` (what features call), `AnalyticsEvent`, `analyticsParams`; in `system/` `AnalyticsSystem` (the systems the app uses) and `AnalyticsSystemClient` (code that sends to one system); in `event/<topic>/` events sent by two or more features.
- **An event** is a class `XxxAnalyticsEvent` that lists its `systems`. It lives in the feature that sends it (`impl/.../analytics/`), or in `:crossfeature:analytics` when two or more features send it.
- **Clients**: `:apps:shop:app` (`analytics/`) holds one client class per `AnalyticsSystem`.
- **Screen views** are sent by `:apps:shop:app` (`navigation/ScreenViews.kt`). Each feature names its screens in `NavDestination.xxxAnalyticsScreen()`; a screen with no name isn't reported.

### DI

[Metro](https://zacsweers.github.io/metro/): the app's graph (`AppScope`) has a base `AppGraph` in `:apps:shop:app` and a graph per platform on top of it, `AndroidAppGraph` in `:apps:shop:android` and `IosAppGraph` in the `iosMain` of `:apps:shop:app`: Metro builds a graph only in platform code.

- **A class to inject** gets `@Inject` on the class, not on the constructor. A class with `@ContributesBinding`, `@ContributesIntoSet` or `@ContributesIntoMap` needs no `@Inject`: the contribution implies it.
- **An implementation of an interface** binds itself with `@ContributesBinding(AppScope::class)` and stays `internal`. Only a third-party type needs a `@Provides` in a `@BindingContainer @ContributesTo(AppScope::class)` object, and that object is public (Metro skips internal ones; the build fails on them).
- **A ViewModel** is `@ViewModelKey @ContributesIntoMap(ViewModelScope::class)`, takes `SavedStateHandle` in its constructor if it needs one, and a screen gets it with `injectedViewModel()` (`:core:compose-utils`, `viewmodel/`).
- **A graph sees only the modules on its module's compile classpath**: a module with contributions, or with `@Inject` classes the graph creates, is a direct dependency of the graph's module, and the types in a module's bindings are `api` (as Room and DataStore in `:crossfeature:data`).

### UI kit

- Its first screen has three groups: design system, shared components, feature components.
- Sections live in `apps/uikit/.../sections/{designsystem,crossfeature,feature}/` and mirror the packages one to one: `core/designsystem/.../components/buttons` ↔ `ButtonsSection`, `crossfeature/ui/.../product` ↔ `ProductSection`, `:feature:cart:ui` ↔ `CartFeatureSection`.
- A new component gets a sample in the section of its package. Design system samples use neutral texts, not the shop's.

### Tests

- Tests follow [`testing.md`](docs/architecture/en/testing.md).
- A test helper (`TestXxx` stand-in, `testXxx()` builder, `xxxTest {}` entry function) used by tests of two or more modules lives next to the module that owns what it replaces: in its `testing` module (`:crossfeature:analytics:testing` → `TestAnalyticsClient`; KMP has no test fixtures). One used by a single module stays in its own tests.
- KMP modules: tests in `src/commonTest` run on the JVM (`testAndroidHostTest`) and on iOS (`iosSimulatorArm64Test`); Robolectric ones go to `src/androidHostTest`. No commas, dots or colons in test names there: Kotlin/Native rejects them. On iOS they can't read Compose resources: `shoppingapp.kmp.compose` turns off their copy so that the test executables stay cacheable.
- The prefix `Fake` is taken by real code: the analytics stand-ins in `:apps:shop:app`.

### build-logic

`build-logic/` is an included build with the convention plugins `shoppingapp.android.{application,compose,screenshots}`, `shoppingapp.kmp.{library,compose,feature}` and `shoppingapp.metro`. compileSdk, minSdk, targetSdk and Java live in `build-logic/src/main/kotlin/AndroidConfig.kt`. Each module declares only its own dependencies.

| Module | Applies |
|---|---|
| `:feature:<name>:impl` | `libs.plugins.shoppingapp.kmp.feature` |
| `:feature:<name>:ui` | `kmp.library` + `kmp.compose` + `screenshots` |
| Kotlin Multiplatform (`:core:*`, `:crossfeature:*`, the `testing` modules) | `kmp.library`; with Compose (`:core:designsystem`, `:core:compose-utils`, `:crossfeature:ui`) + `kmp.compose` |
| any module with DI (`@Inject`, `@Contributes*`), except `impl` (`feature` applies it) | + `metro` |

A library module with `@Preview`s applies `screenshots`: screenshot tests made from the previews. `:apps:uikit` has none of its own.

## Server

`server/` is the Ktor backend for the app (Kotlin 2.4, Ktor 3.6, data in JSON files): a separate Gradle build with its own wrapper and version catalog, not included in `settings.gradle.kts`. The API's DTOs live in `contract/`, a KMP build (JVM, iOS) that both the app and the server include (`includeBuild`); the app keeps only the mappers to its domain models. API, data rules and deploy: [`server/README.md`](server/README.md).

## Build

The main commands, from the project root:

| Command | Does |
|---|---|
| `./gradlew :apps:shop:android:assembleDebug` | builds the debug APK: `apps/shop/android/build/outputs/apk/debug/shop-debug.apk` |
| `./gradlew :apps:shop:android:installDebug` | builds and installs it on the connected device |
| `./gradlew :apps:uikit:installDebug` | builds and installs the UI kit; in Android Studio, the run configuration of the `uikit` module |
| `./gradlew testAndroidHostTest` | every app test on the JVM (the libraries are KMP modules, which have no `test`); screenshots compared with `<module>/screenshots/` |
| `./gradlew compileKotlinIosSimulatorArm64` | compiles the shared code for iOS; works on Linux, catches iOS errors before CI |
| `./gradlew iosSimulatorArm64Test` | the common tests on the iOS simulator (Mac only; CI runs them on macOS) |
| `cd server && ./gradlew test` | server tests |
| `server/deploy.sh` | tests, builds and restarts the deployed server |

The CI checks locally (CI treats Kotlin and lint warnings as errors):

```sh
./gradlew -PwarningsAsErrors=true spotlessCheck assertModuleGraph buildHealth lintDebug lintAndroidMain testAndroidHostTest :apps:shop:android:assembleDebug :apps:uikit:assembleDebug compileKotlinIosSimulatorArm64
cd server && ./gradlew -PwarningsAsErrors=true spotlessCheck test
```

> [!WARNING]
> Never run two Gradle builds in the same checkout at once: they share `build/` dirs and corrupt each other's outputs.

### Screenshots after a UI change

1. Check that only the screens meant to change did, either way:
   - before recording: `./gradlew testAndroidHostTest` fails only those previews and leaves `build/outputs/roborazzi/*_compare.png`;
   - after recording: `git status` shows only their images.
2. Record: `./gradlew recordRoborazziAndroidHostTest -Proborazzi.cleanupOldScreenshots=true` saves the new screenshots and deletes those of removed previews; in Android Studio, the Record screenshots run configuration.
3. Commit the images with the change.

- **Don't record when a change wasn't meant to alter the UI**: a failed screenshot test is then a real problem.
- **Never put the cleanup flag in `gradle.properties`**: a filtered test run would then delete the module's other screenshots. Pass it only with the record tasks: Roborazzi also hooks the iOS test tasks of KMP modules, which would delete every saved image.
