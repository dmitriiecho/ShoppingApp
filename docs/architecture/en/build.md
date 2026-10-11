# Build

[Русская версия](../ru/build.md) · [All pages](README.md)

The shared module setup lives in [`build-logic/`](../../../build-logic/), an included build with convention plugins. Each module applies a few of them and declares only its own dependencies.

&nbsp;

## Convention plugins

Each kind of module has its own plugin:

| Plugin | For | Adds |
|---|---|---|
| `shoppingapp.android.application` | `:apps:shop`, `:apps:uikit` | the Android setup, targetSdk, signing, R8 in release, the app's languages for the system settings, the [module graph check](modules.md#the-check), dependency analysis, warnings as errors in CI |
| `shoppingapp.android.library` | every Android library | compileSdk, minSdk, Java, dependency analysis, warnings as errors in CI, the test setup: unit tests on debug only, [Robolectric](testing.md#database), the [API samples](testing.md#contract-with-the-server), the message of a failed test in the log |
| `shoppingapp.android.compose` | modules with Compose | the Compose compiler, the BOM, Material 3, Slack's Compose lint rules |
| `shoppingapp.metro` | modules with DI, Android or pure Kotlin | Metro: an `internal` contributed class stays `internal`, an `internal` binding container fails the build |
| `shoppingapp.android.feature` | every `:feature:<name>:impl` | library + Compose + Metro + serialization, navigation and lifecycle libraries |
| `shoppingapp.android.screenshots` | every library module with previews | [screenshot tests](testing.md#screenshots) generated from the previews |
| `shoppingapp.jvm.library` | `:shared:domain`, `:shared:analytics`, `:core:config` | Kotlin JVM without Android, dependency analysis, warnings as errors in CI, the message of a failed test in the log |

As a result, a module's build file is short:

```kotlin
// feature/promo/ui/build.gradle.kts
plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.compose)
    alias(libs.plugins.shoppingapp.android.screenshots)
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

&nbsp;

## Code style

[ktlint](https://github.com/pinterest/ktlint) 1.8 checks the style through Spotless, using the rules in the root [`.editorconfig`](../../../.editorconfig):

```sh
./gradlew spotlessCheck   # check
./gradlew spotlessApply   # fix
```

- **The `android_studio` style**, which matches Android Studio's formatter, so Ctrl+Alt+L and ktlint agree.
- **120 characters per line.**
- **Trailing commas** in multi-line lists, so diffs stay one line long and lines can be reordered freely.
- **One parameter per line** once there are two or more.

Comments are in English and short, and they explain what the code itself can't: a reason, an order that matters, a workaround. Obvious code has no comments.

&nbsp;

## CI

[`ci.yml`](../../../.github/workflows/ci.yml) runs on every pull request and every push to `main` and the variant branches (`kmp-ready`, `kmp`), unless only the docs, READMEs or their images changed. Two jobs run in parallel:

| Job | Steps |
|---|---|
| Android | code style (ktlint) → dependencies between modules (`assertModuleGraph`) → unused dependencies (`buildHealth`) → problems in code and resources (Android lint with Slack's Compose rules) → [unit and screenshot tests](testing.md) → both apps compile |
| Server | code style (ktlint) → API and data files (tests, including checks of `data/*.json` and the [API samples](testing.md#contract-with-the-server)) |

- **Every check runs even if an earlier one failed**, so a single run shows all the problems.
- **Warnings are errors in CI**, both Kotlin's and lint's: CI passes `-PwarningsAsErrors=true`, which is read by the convention plugins ([`WarningsAsErrors.kt`](../../../build-logic/src/main/kotlin/WarningsAsErrors.kt)) and by the server build. A local build only prints the warnings.
- **Unused dependencies** are found by the [Dependency Analysis](https://github.com/autonomousapps/dependency-analysis-gradle-plugin) plugin; its rules, including which advice is ignored and why, are in the root [`build.gradle.kts`](../../../build.gradle.kts).
- **Lint exceptions** are in the root [`lint.xml`](../../../lint.xml): the CompositionLocals the project creates on purpose, and "a newer version is available", which is downgraded to a hint so that a new library release doesn't fail CI.
- **Failed tests show up where you'd look anyway**: each one, with its message and line, at the top of the run's page; next to the test's line in the PR's Files changed tab, if the test file is part of the PR; and in a summary below, with the full stack trace ([action-junit-report](https://github.com/mikepenz/action-junit-report), the only action in CI not made by GitHub or Gradle).
- **The Android runtime that Robolectric uses is cached** between runs: Robolectric downloads it into `~/.m2`, which Gradle's cache doesn't cover.
- **A job is stopped after 30 minutes** (the server job after 15) instead of hanging for GitHub's default 6 hours.

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

## Commands

The main commands, from the project root:

```sh
./gradlew :apps:shop:installDebug      # install the shop on a connected device
./gradlew :apps:uikit:installDebug     # install the UI kit
./gradlew assertModuleGraph            # check dependencies between modules
./gradlew buildHealth                  # find unused dependencies
./gradlew lintDebug                    # run Android lint on every module
./gradlew test                         # run the app tests, including screenshots
./gradlew recordRoborazziDebug -Proborazzi.cleanupOldScreenshots=true  # record new screenshots after a UI change
cd server && ./gradlew test            # run the server tests
```

The same checks as CI, with warnings treated as errors:

```sh
./gradlew -PwarningsAsErrors=true spotlessCheck assertModuleGraph buildHealth lintDebug test :apps:shop:assembleDebug :apps:uikit:assembleDebug
cd server && ./gradlew -PwarningsAsErrors=true spotlessCheck test
```
