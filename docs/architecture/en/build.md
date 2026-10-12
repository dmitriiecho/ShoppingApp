[Русская версия](../ru/build.md) · [All pages](README.md)

> **Variant `kmp`**: Android and iOS, Metro, Ktor
>
> This page in the other variants:
> - [`main`](https://github.com/dmitriiecho/ShoppingApp/blob/main/docs/architecture/en/build.md): Android, Hilt, Retrofit
> - [`kmp-ready`](https://github.com/dmitriiecho/ShoppingApp/blob/kmp-ready/docs/architecture/en/build.md): Android, Metro, Ktor

# Build

The shared module setup lives in [`build-logic/`](../../../build-logic/), an included build with convention plugins. Each module applies a few of them and declares only its own dependencies.

&nbsp;

## Convention plugins

Each kind of module has its own plugin:

| Plugin | For | Adds |
|---|---|---|
| `shoppingapp.android.application` | `:apps:shop:android`, `:apps:uikit:android` | the Android setup, targetSdk, signing, R8 in release, the app's languages for the system settings (the list is generated from the resources; the apps' strings are Compose resources, so each lists its languages itself, in `res/xml/locale_config.xml`), the [module graph check](modules.md#the-check), dependency analysis, warnings as errors in CI |
| `shoppingapp.android.compose` | `:apps:shop:android`, `:apps:uikit:android` | the Compose compiler, the BOM, Material 3, Slack's Compose lint rules |
| `shoppingapp.metro` | modules with DI, Android or KMP | Metro: an `internal` contributed class stays `internal`, an `internal` binding container fails the build |
| `shoppingapp.android.screenshots` | every library with previews | [screenshot tests](testing.md#screenshots) generated from the previews |
| `shoppingapp.kmp.library` | every Kotlin Multiplatform library: `:core:*`, `:crossfeature:*`, `:feature:<name>:ui` and the `testing` modules | Android and iOS targets, the Android setup and [Robolectric](testing.md#database) for host tests, Android lint for the common code too, dependency analysis, warnings as errors in CI, the message of a failed test in the log |
| `shoppingapp.kmp.compose` | KMP modules with Compose: `:core:designsystem`, `:core:compose-utils`, `:crossfeature:ui`, `:feature:<name>:ui`, the apps' shared `:apps:shop:app` and `:apps:uikit:app` | Compose Multiplatform, its core libraries and resources (`Res`), Slack's Compose lint rules |
| `shoppingapp.kmp.feature` | every `:feature:<name>:impl` | `kmp.library` + `kmp.compose` + Metro + serialization, JetBrains' navigation and lifecycle libraries for KMP |

As a result, a module's build file is short:

```kotlin
// feature/promo/ui/build.gradle.kts
plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
    alias(libs.plugins.shoppingapp.kmp.compose)
    alias(libs.plugins.shoppingapp.android.screenshots)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:designsystem"))
        }
    }
}
```

The SDK and Java versions are set in one place, [`AndroidConfig.kt`](../../../build-logic/src/main/kotlin/AndroidConfig.kt):

```kotlin
internal object AndroidConfig {
    const val COMPILE_SDK = 37
    const val MIN_SDK = 26
    const val TARGET_SDK = 37
    val JAVA_VERSION = JavaVersion.VERSION_21
}
```

&nbsp;

## Versions and repositories

All versions are in [`gradle/libs.versions.toml`](../../../gradle/libs.versions.toml), and build-logic reads the same catalog. Modules apply plugins without specifying versions.

- **Repositories are declared only in `settings.gradle.kts`**: a module that declares its own breaks the build. Google Maven is used only for Google and AndroidX artifacts.
- **The configuration cache, build cache and parallel builds are enabled.** The build cache stores task outputs, and CI reuses them from earlier runs as well.
- **The server is a separate build** with its own wrapper and version catalog; the app's settings don't include it.
- **The contract (`contract/`) is a separate build too**, included by both the app and the server (`includeBuild`): the API's DTOs, in Kotlin Multiplatform for the server's JVM and the app's Android and iOS. It reads the app's version catalog, so both sides build it with the same Kotlin.

&nbsp;

## Code style

[ktlint](https://github.com/pinterest/ktlint) 1.8 checks the style through Spotless, using the rules in the root [`.editorconfig`](../../../.editorconfig):

```sh
./gradlew spotlessCheck  # check
./gradlew spotlessApply  # fix
```

- **The `android_studio` style**, which matches Android Studio's formatter, so Ctrl+Alt+L and ktlint agree.
- **120 characters per line.**
- **Trailing commas** in multi-line lists, so diffs stay one line long and lines can be reordered freely.
- **One parameter per line** once there are two or more.

Comments are in English and short, and they explain what the code itself can't: a reason, an order that matters, a workaround. Obvious code has no comments.

&nbsp;

## CI

[`ci.yml`](../../../.github/workflows/ci.yml) runs on every pull request and every push to `main` and the variant branches (`kmp-ready`, `kmp`), unless only the docs, READMEs or their images changed. Three jobs run in parallel:

| Job | Steps |
|---|---|
| Android | code style (ktlint) → dependencies between modules (`assertModuleGraph`) → unused dependencies (`buildHealth`) → problems in code and resources (Android lint with Slack's Compose rules) → [unit and screenshot tests](testing.md) → both apps compile |
| iOS | the common tests of every KMP module on the iOS simulator → the [iOS app](#ios-app) builds (`xcodebuild`); on macOS, free for a public repository |
| Server | code style (ktlint) → API and data files (tests, including checks of `data/*.json` and the [API samples](testing.md#contract-with-the-server)) |

- **Every check runs even if an earlier one failed**, so a single run shows all the problems.
- **Warnings are errors in CI**, both Kotlin's and lint's: CI passes `-PwarningsAsErrors=true`, which is read by the convention plugins ([`WarningsAsErrors.kt`](../../../build-logic/src/main/kotlin/WarningsAsErrors.kt)) and by the server build. A local build only prints the warnings.
- **Unused dependencies** are found by the [Dependency Analysis](https://github.com/autonomousapps/dependency-analysis-gradle-plugin) plugin; its rules, including which advice is ignored and why, are in the root [`build.gradle.kts`](../../../build.gradle.kts).
- **Lint exceptions** are in the root [`lint.xml`](../../../lint.xml): the CompositionLocals the project creates on purpose, and "a newer version is available", which is downgraded to a hint so that a new library release doesn't fail CI.
- **Failed tests show up where you'd look anyway**: each one, with its message and line, at the top of the run's page; next to the test's line in the PR's Files changed tab, if the test file is part of the PR; and in a summary below, with the full stack trace ([action-junit-report](https://github.com/mikepenz/action-junit-report), the only action in CI not made by GitHub or Gradle).
- **The Android runtime that Robolectric uses is cached** between runs: Robolectric downloads it into `~/.m2`, which Gradle's cache doesn't cover. Its key is what it depends on, the Robolectric version and the targetSdk, so an unrelated new library doesn't make a new copy. The iOS job caches the Kotlin/Native compiler together with the caches of the libraries it builds while linking, a few minutes of work: that cache is saved after the Xcode build, keyed by the Kotlin version and the version catalog, and a new catalog starts from the latest copy of the same Kotlin version.
- **The caches are saved only on pushes to `main` and `kmp`**, where work goes on; pull requests and the other branches only read them, a pull request its base branch's. GitHub keeps 10 GB of caches per repository and drops the oldest beyond that, and a pull request's own copy would serve no other run.
- **The iOS job saves Gradle's cache without cleaning it up.** The cleanup of `setup-gradle` drops the cached test executables of iOS even when the run took them from the cache, and they would be linked again on every run; Gradle itself removes the entries unused for 7 days.
- **A job is stopped after 30 minutes** (the iOS job after 45, the server job after 15) instead of hanging for GitHub's default 6 hours.

A new push to a pull request cancels that pull request's run if it is still in progress, while runs on branches always finish:

```yaml
concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: ${{ github.event_name == 'pull_request' }}
```

[`latest-build.yml`](../../../.github/workflows/latest-build.yml) builds the release APKs of the app and the UI kit after CI passes on a push to `main`, `kmp-ready` or `kmp`, and publishes them as the `<branch>-latest` release. The download links in the README point there. The `main` build is the repo's Latest release; the other branches' builds are pre-releases.

[`dependency-graph.yml`](../../../.github/workflows/dependency-graph.yml) sends GitHub the list of libraries the app and the server ship with, after every push to `main`. GitHub checks them for known vulnerabilities and shows a Dependabot alert in the Security tab if it finds any. It is a separate workflow because sending the list needs write access, and CI stays read-only.

[`screenshot-comment.yml`](../../../.github/workflows/screenshot-comment.yml) posts a pull request's changed screenshots in a comment on it when CI fails because of them. A comment can only show an image that has a URL, so the images are pushed to a `screenshots/pr-<N>` branch, which is deleted once the screenshots match again or the pull request is closed. Each pull request has a single comment that is updated on every run. This workflow also needs write access, so it never runs the pull request's code: it only copies the images CI produced.

&nbsp;

## Signing

The debug key is stored in the repo ([`build-logic/debug.keystore`](../../../build-logic/debug.keystore)), so a build from any computer has the same signature. App Links depend on this: the key's fingerprint is listed in `assetlinks.json`.

- **Release builds are signed with the same key** so they can be installed on an emulator; publishing to a store would need a real key.
- **R8 is enabled for release builds** and shrinks the code and resources.

&nbsp;

## iOS app

The iOS app is the Xcode project in [`apps/shop/ios`](../../../apps/shop/ios/). It shows the screens of `:apps:shop:app`, which builds them into the `ShopApp` framework: the project's Run Script calls Gradle (`embedAndSignAppleFrameworkForXcode`) before Xcode compiles the Swift code. The app is the whole shop: `MainViewController()` in `iosMain` creates the app's graph (`IosAppGraph`) and shows `App()`, as `MainActivity` does on Android.

- **It builds on a Mac** with Xcode and JDK 21: open `apps/shop/ios/iosApp.xcodeproj` and run it on a simulator, or use the iOS run configuration of Android Studio with the Kotlin Multiplatform plugin.
- **On Linux the shared code compiles for iOS too** (`./gradlew compileKotlinIosSimulatorArm64`), which catches most iOS errors before CI; linking the framework, the iOS tests and the Xcode build need a Mac, so CI runs them on macOS.
- **iOS 18.5 or later**, iPhone, portrait only. The server is reached over plain HTTP by its IP address, which iOS allows only with `NSAllowsArbitraryLoads` in `Info.plist`.
- **A missing function fails the link**: by default Kotlin/Native links a call to a function the libraries lack (e.g. one written under another signature) into a call that throws at run time; `shoppingapp.kmp.library` passes `-Xpartial-linkage-loglevel=ERROR`, so such a framework doesn't build. A compiler bug once made the app crash at start this way.
- **Logs, as on Android**: a debug build writes HTTP requests and analytics events to the Xcode console, a release build writes nothing. A KMP module has no build variants, so the check is `Platform.isDebugBinary` at run time; Xcode's Release configuration builds the release framework.

&nbsp;

## Commands

The main commands, from the project root:

```sh
./gradlew :apps:shop:android:installDebug   # install the shop on a connected device
./gradlew :apps:uikit:android:installDebug  # install the UI kit
./gradlew assertModuleGraph                 # check dependencies between modules
./gradlew buildHealth                       # find unused dependencies
./gradlew lintDebug lintAndroidMain         # run Android lint on the Android and the KMP modules
./gradlew testAndroidHostTest               # run the app tests on the JVM, including screenshots
./gradlew compileKotlinIosSimulatorArm64    # compile the shared code for iOS (on Linux too)
./gradlew iosSimulatorArm64Test             # run the common tests on the iOS simulator (on a Mac)
./gradlew recordRoborazziAndroidHostTest -Proborazzi.cleanupOldScreenshots=true  # record new screenshots after a UI change
cd server && ./gradlew test                 # run the server tests
```

The same checks as CI, with warnings treated as errors; on Linux, compiling the iOS code stands in for the iOS job:

```sh
./gradlew -PwarningsAsErrors=true spotlessCheck assertModuleGraph buildHealth lintDebug lintAndroidMain testAndroidHostTest :apps:shop:android:assembleDebug :apps:uikit:android:assembleDebug compileKotlinIosSimulatorArm64
cd server && ./gradlew -PwarningsAsErrors=true spotlessCheck test
```
