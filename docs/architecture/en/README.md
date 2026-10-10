# Architecture

[Русская версия](../ru/README.md)

ShoppingApp is a demo store for Android: a searchable catalog, product details, a cart that lives on the device and is checked against the server, promo codes and a checkout form. A small Ktor server in [`server/`](../../../server/README.md) provides the catalog and checks the cart.

These pages explain how the app is built and why. Each one covers the rules, the reasons behind them and short excerpts from the project's code.

&nbsp;

## Modules at a glance

The app is organised in four levels, from the bottom up:

```text
apps/     shop, uikit                                    the applications
feature/  catalog, cart, promo, checkout, settings       each one is ui + impl
shared/   domain, data, ui, analytics                    shop code used by two or more features
core/     designsystem, compose-utils, network, config   knows nothing about the shop
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
| [Build](build.md) | convention plugins, the version catalog, code style, CI, signing |
| [Testing](testing.md) | how tests are written, where test helpers live and how they are named, tools, kinds of tests |

&nbsp;

## Stack

The main libraries and tools:

| Area | Choice |
|---|---|
| Language and UI | Kotlin 2.4, Jetpack Compose, Material 3 |
| Structure | MVVM with a single UI state per screen and one-off effects |
| DI | Hilt (KSP) |
| Navigation | Navigation Compose with type-safe routes |
| Data | Room, DataStore, Paging 3 |
| Network | Ktor Client (OkHttp engine), kotlinx.serialization, Coil for images |
| Build | Gradle 9.8, AGP 9.4, JDK 21, convention plugins; minSdk 26, targetSdk 37, compileSdk 37 |
| Server | Ktor 3.6, data in JSON files |
| Tests | kotlin.test, AssertK, kotlinx-coroutines-test, Turbine, Ktor MockEngine, Robolectric, Roborazzi with ComposablePreviewScanner |
