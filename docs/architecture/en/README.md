[Русская версия](../ru/README.md)

> **Variant `kmp`**: Android and iOS, Metro, Ktor
>
> This page in the other variants:
> - [`main`](https://github.com/dmitriiecho/ShoppingApp/blob/main/docs/architecture/en/README.md): Android, Hilt, Retrofit
> - [`kmp-ready`](https://github.com/dmitriiecho/ShoppingApp/blob/kmp-ready/docs/architecture/en/README.md): Android, Metro, Ktor

# Architecture

ShoppingApp is a demo store for Android and iOS: a searchable catalog, product details, a cart that lives on the device and is checked against the server, promo codes and a checkout form. A small Ktor server in [`server/`](../../../server/README.md) provides the catalog and checks the cart. Both platforms run the same Kotlin Multiplatform code: screens, logic and data; only the entry points and a few platform details are their own.

These pages explain how the app is built and why. Each one covers the rules, the reasons behind them and short excerpts from the project's code.

&nbsp;

## Modules at a glance

The app is organised in four levels, from the bottom up:

```text
apps/         shop, uikit (each: shared, android, ios)       the applications: shared code and each platform's entry
feature/      catalog, cart, promo, checkout, settings       each one is ui + impl
crossfeature/ domain, data, ui, analytics                    shop code used by two or more features
core/         designsystem, compose-utils, network, config   knows nothing about the shop
```

A module never depends on a level above its own, and features never depend on each other. The build enforces both rules.

&nbsp;

## Pages

Each page can be read on its own:

| Page | What it covers |
|---|---|
| [Modules](modules.md) | the levels, where new code goes, what a feature exposes, the module graph check |
| [Navigation](navigation.md) | how the app connects the features, tabs, screen results, deep links |
| [Screens](screens.md) | how a screen is split into files, UI state, events and effects, text fields, Compose stability, wide screens |
| [Data](data.md) | use cases, repositories, error types, local storage, network, prices, the contract with the server |
| [Analytics](analytics.md) | events, analytics systems, screen views, what is allowed to be sent |
| [Build](build.md) | convention plugins, the version catalog, code style, CI, signing, the iOS apps |
| [Testing](testing.md) | how tests are written, where test helpers live and how they are named, tools, kinds of tests |

&nbsp;

## Stack

The main libraries and tools:

| Area | Choice |
|---|---|
| Platforms | Android (minSdk 26), iOS 18.5+ (iPhone); shared code in Kotlin Multiplatform |
| Language and UI | Kotlin 2.4, Compose Multiplatform 1.12 (Jetpack Compose on Android), Material 3 |
| Structure | MVVM with a single UI state per screen and one-off effects |
| DI | Metro (compile-time, a Kotlin compiler plugin) |
| Navigation | Navigation Compose (JetBrains' multiplatform build) with type-safe routes |
| Data | Room, DataStore, Paging 3 |
| Network | Ktor Client (OkHttp engine on Android, Darwin on iOS), kotlinx.serialization, Coil for images |
| Logging | Kermit (logcat, the Xcode console) |
| Build | Gradle 9.8, AGP 9.4, JDK 21, convention plugins; targetSdk 37, compileSdk 37; Xcode for the iOS apps |
| Server | Ktor 3.6, data in JSON files |
| Tests | kotlin.test, AssertK, kotlinx-coroutines-test, Turbine, Ktor MockEngine, Robolectric, Roborazzi with ComposablePreviewScanner |
