# Architecture

[Русская версия](../ru/README.md)

ShoppingApp is a demo shop for Android: a catalog with search, product details, a cart that the server checks before checkout, promo codes and a checkout form. A small Ktor server in [`server/`](../../../server/README.md) serves the catalog and checks the cart.

These pages explain how the app is built and why: each one has the rules, the reasons behind them and short pieces of the project's code.

&nbsp;

## Modules at a glance

The app is laid out in four levels, from the bottom up:

```text
apps/     shop, uikit                                    the applications
feature/  catalog, cart, promo, checkout, settings       each one is ui + impl
shared/   domain, data, ui, analytics                    shop code used by two or more features
core/     designsystem, compose-utils, network, config   knows nothing about the shop
```

A module depends only on modules of its own level or below, and the build checks it.

&nbsp;

## Pages

Each page can be read on its own:

| Page | What it covers |
|---|---|
| [Modules](modules.md) | the levels, where new code goes, what a feature shows outside, the module graph check |
| [Navigation](navigation.md) | how the app joins the features, tabs, screen results, deep links |
| [Screens](screens.md) | the files of a screen, UI state, events and effects, text fields, Compose stability |
| [Data](data.md) | use cases, repositories, error types, local storage, network, prices, the contract with the server |
| [Analytics](analytics.md) | events, analytics systems, screen views, what may be sent |
| [Build](build.md) | convention plugins, the version catalog, code style, CI, signing |

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
| Network | Retrofit, OkHttp, kotlinx.serialization, Coil for images |
| Build | Gradle 9.8, AGP 9.4, JDK 21, convention plugins; minSdk 26, targetSdk 36, compileSdk 37 |
| Server | Ktor 3.6, data in JSON files |
