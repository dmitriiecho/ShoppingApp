# Build

[Русская версия](../ru/build.md) · [All pages](README.md)

The common setup of modules lives in [`build-logic/`](../../../build-logic/), an included build with convention plugins. A module applies one or two plugins and declares only its own dependencies.

&nbsp;

## Convention plugins

Each kind of module has its own plugin:

| Plugin | For | Adds |
|---|---|---|
| `shoppingapp.android.application` | `:apps:shop`, `:apps:uikit` | the Android setup, targetSdk, signing, R8 in release, the [module graph check](modules.md#the-check), dependency analysis, warnings as errors in CI |
| `shoppingapp.android.library` | every Android library | compileSdk, minSdk, Java, dependency analysis, warnings as errors in CI, the test setup: unit tests on debug only, [Robolectric](testing.md#database), the [API samples](testing.md#contract-with-the-server), a failed test's message in the log |
| `shoppingapp.android.compose` | modules with Compose | the Compose compiler, the BOM, Material 3, Slack's Compose lint rules |
| `shoppingapp.android.hilt` | modules with Hilt | Hilt with KSP |
| `shoppingapp.android.feature` | every `:feature:<name>:impl` | library + Compose + Hilt + serialization, navigation and lifecycle libraries |
| `shoppingapp.android.screenshots` | every module with previews | [screenshot tests](testing.md#screenshots) made from the previews |
| `shoppingapp.jvm.library` | `:shared:domain`, `:shared:analytics`, `:core:config` | Kotlin JVM without Android, dependency analysis, warnings as errors in CI, a failed test's message in the log |

So a module's build file is short:

```kotlin
// feature/promo/ui/build.gradle.kts
plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.compose)
}

android {
    namespace = "krio.systemdesign.shoppingapp.feature.promo.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
}
```

The SDK and Java versions are set in one place, [`AndroidConfig.kt`](../../../build-logic/src/main/kotlin/AndroidConfig.kt):

```kotlin
internal object AndroidConfig {
    const val COMPILE_SDK = 37
    const val MIN_SDK = 26
    const val TARGET_SDK = 36
    val JAVA_VERSION = JavaVersion.VERSION_21
}
```

&nbsp;

## Versions and repositories

Every version is in [`gradle/libs.versions.toml`](../../../gradle/libs.versions.toml), and build-logic reads the same catalog. Modules apply plugins without versions.

- **Repositories are declared only in `settings.gradle.kts`**: a module that declares its own fails the build. Google Maven serves only Google and AndroidX artifacts.
- **Configuration cache, build cache and parallel builds are on.** The build cache keeps task outputs, and CI reuses them from earlier runs too.
- **The server is a separate build** with its own wrapper and catalog, not included in the app's settings.

&nbsp;

## Code style

[ktlint](https://pinterest.github.io/ktlint/) 1.8 checks the style through Spotless, with the rules in the root [`.editorconfig`](../../../.editorconfig):

```sh
./gradlew spotlessCheck   # check
./gradlew spotlessApply   # fix
```

- **The `android_studio` style**, the same as Android Studio's formatter, so Ctrl+Alt+L and ktlint agree.
- **120 characters per line.**
- **Trailing commas** in multi-line lists: one-line diffs, and lines can be reordered.
- **One parameter per line once there are two or more.**

Comments are in English, short, and explain what the code can't say: a reason, an order that matters, a workaround. Obvious code gets none.

&nbsp;

## CI

[`ci.yml`](../../../.github/workflows/ci.yml) runs on every pull request and every push to `main` and the variant branches (`kmp-ready`, `kmp`), unless only docs, READMEs or their images changed. Two jobs run in parallel:

| Job | Steps |
|---|---|
| Android | code style (ktlint) → dependencies between modules (`assertModuleGraph`) → unused dependencies (`buildHealth`) → problems in code and resources (Android lint with Slack's Compose rules) → [unit and screenshot tests](testing.md) → both apps compile |
| Server | code style (ktlint) → API and data files (tests, including the check of `data/*.json` and the [API samples](testing.md#contract-with-the-server)) |

- **Every check runs even if one before it failed**, so one run shows all the problems.
- **Warnings are errors in CI**, Kotlin's and lint's alike: CI passes `-PwarningsAsErrors=true`, read by the convention plugins ([`WarningsAsErrors.kt`](../../../build-logic/src/main/kotlin/WarningsAsErrors.kt)) and by the server build. A local build only prints them.
- **Unused dependencies** are found by the [Dependency Analysis](https://github.com/autonomousapps/dependency-analysis-gradle-plugin) plugin; its rules, with the advice it ignores and why, are in the root [`build.gradle.kts`](../../../build.gradle.kts).
- **Lint exceptions** are in the root [`lint.xml`](../../../lint.xml): the CompositionLocals the project creates on purpose, and "a newer version is available", reported as a hint so a library's release doesn't fail CI.
- **Failed tests are shown where you look anyway**: each one, with its message and line, at the top of the run's page, next to the test's line in the PR's Files changed when the test file is among the PR's changes, and in a summary below with the full stack trace ([action-junit-report](https://github.com/mikepenz/action-junit-report), the one action in CI that isn't GitHub's or Gradle's).
- **Robolectric's Android is cached** between runs: Robolectric downloads it into `~/.m2`, which Gradle's cache doesn't cover.
- **A job is stopped after 30 minutes** (the server's after 15) instead of hanging for GitHub's default 6 hours.

A new push to a pull request cancels its run that is still going, and runs on the branches always finish:

```yaml
concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: ${{ github.event_name == 'pull_request' }}
```

[`latest-build.yml`](../../../.github/workflows/latest-build.yml) builds the release APKs of the app and the UI kit after CI passes on a push to `main`, `kmp-ready` or `kmp`, and publishes them as the release `<branch>-latest`. The download links in the README point to it. The `main` build is the repo's Latest release, the other branches are pre-releases.

[`dependency-graph.yml`](../../../.github/workflows/dependency-graph.yml) sends GitHub the libraries the app and the server ship with, after every push to `main`. GitHub checks them for known vulnerabilities and shows a Dependabot alert in the Security tab if one has any. It is a separate workflow because sending needs write access, and CI stays read-only.

[`screenshot-comment.yml`](../../../.github/workflows/screenshot-comment.yml) shows a pull request's changed screenshots in a comment on it, after CI fails on them. A comment shows only an image that has a link, so the images are pushed to a branch `screenshots/pr-<N>`, deleted when the screenshots match again or the pull request is closed. The pull request has one comment, updated on every run. It needs write access too, and it never runs the pull request's code: it only copies the images CI made.

&nbsp;

## Signing

The debug key is in the repo ([`build-logic/debug.keystore`](../../../build-logic/debug.keystore)), so a build from any computer has the same signature. App Links depend on it: the key's fingerprint is in `assetlinks.json`.

- **Release is signed with the same key** so it installs on an emulator; publishing to a store would need a real key.
- **R8 is on in release**: it shrinks code and resources.

&nbsp;

## Commands

The main commands, from the project root:

```sh
./gradlew :apps:shop:installDebug      # the shop on a connected device
./gradlew :apps:uikit:installDebug     # the UI kit app
./gradlew assertModuleGraph            # dependencies between modules
./gradlew buildHealth                  # unused dependencies
./gradlew lintDebug                    # Android lint for every module
./gradlew test                         # app tests, screenshots included
./gradlew recordRoborazziDebug -Proborazzi.cleanupOldScreenshots=true  # new screenshots after a UI change
cd server && ./gradlew test            # server tests
```

The same checks as CI, with warnings as errors:

```sh
./gradlew -PwarningsAsErrors=true spotlessCheck assertModuleGraph buildHealth lintDebug test :apps:shop:assembleDebug :apps:uikit:assembleDebug
cd server && ./gradlew -PwarningsAsErrors=true spotlessCheck test
```
