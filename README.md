# ShoppingApp

[![CI](https://github.com/dmitriiecho/ShoppingApp/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/dmitriiecho/ShoppingApp/actions/workflows/ci.yml)

[Русская версия](README.ru.md)

ShoppingApp is an example of an Android app built on a current stack. The repo has two apps:

- **The shop**, a demo shop: a catalog with search, a cart kept on the device and checked against the server, promo codes and checkout. It runs on phones and tablets.
- **The UI kit**, a showcase of the design: every styled component of the shop, section by section, with a light and dark theme switch. It is there to look through and check the design without walking the shop's flows.

&nbsp;

## Variants

The same app is built on three slightly different stacks, each variant in its own branch:

| Branch | Stack | Download |
|---|---|---|
| [`main`](https://github.com/dmitriiecho/ShoppingApp/tree/main) | Android: Hilt, Retrofit + OkHttp, Room, Navigation Compose | [APK](https://github.com/dmitriiecho/ShoppingApp/releases/download/main-latest/ShoppingApp.apk), [UI kit APK](https://github.com/dmitriiecho/ShoppingApp/releases/download/main-latest/ShoppingApp-UiKit.apk) |
| `kmp-ready` | Android, with libraries replaced by ones that work in Kotlin Multiplatform | coming |
| `kmp` | Kotlin Multiplatform | coming |

Each branch has two APKs: the shop and the UI kit.

&nbsp;

## How it looks

The three main flows, as they run in the app, from left to right: the catalog, the cart and a promo code, server changes and checkout.

<p>
  <img src=".github/readme/catalog.webp" width="240" alt="Opening a product and going back, scrolling the catalog, opening another product and adding two to the cart">
  <img src=".github/readme/promo.webp" width="240" alt="The cart with two laptop stands, applying a promo code from the hint and the discount in the totals">
  <img src=".github/readme/checkout.webp" width="240" alt="The cart shows that the price and the stock changed on the server: accepting the new price, lowering the quantity to what is left and opening checkout">
</p>

&nbsp;

## What's inside

The app is small, but every screen has something to look at:

- **Catalog**: search that waits for a pause in typing, pages loaded from the server, the product image flying into its details.
- **Cart**, stored only on the device (Room): the server keeps no carts, it only checks this one each time the cart opens and before checkout: items gone, new prices, not enough stock.
- **Promo codes** checked by the server, with the discount right in the totals.
- **Checkout**: the form survives process death, and a double tap won't place the order twice.
- **Deep links** to the catalog, the cart and a product, with verified App Links.
- **Settings**: the theme, a request delay and test items that put each possible change into the cart.
- **Tablets and wide screens**: the content stays a column in the middle, and the product screen held sideways puts the image and the details side by side ([more](docs/architecture/en/screens.md#wide-screens)).

&nbsp;

## Architecture

Modules are laid out in four levels, and the build checks that no module depends on a level above its own:

```text
apps/     shop, uikit                                    the applications
feature/  catalog, cart, promo, checkout, settings       each one is ui + impl
shared/   domain, data, ui, analytics                    shop code used by two or more features
core/     designsystem, compose-utils, network, config   knows nothing about the shop
```

How the modules, navigation, screens, data, analytics and the build work is in the [architecture docs](docs/architecture/en/README.md).

&nbsp;

## Tests and CI

- **Tests** on the JVM: the logic, the ViewModels, the data layer, the contract with the server and a screenshot of every preview.
- **CI** on every pull request: the code style, the module rules, unused dependencies, lint, the tests and the build of both apps. It shows the changed screenshots in a comment on the pull request, and after every change in `main` it rebuilds the APKs.

&nbsp;

## Running

The easiest way is to download the [APK from `main`](https://github.com/dmitriiecho/ShoppingApp/releases/download/main-latest/ShoppingApp.apk): a release build for Android 8.0 and newer, rebuilt by CI after every code change in `main`. The UI kit app, with every styled component, is there too: [UI kit APK](https://github.com/dmitriiecho/ShoppingApp/releases/download/main-latest/ShoppingApp-UiKit.apk).

To build it yourself you need JDK 21 and the Android SDK:

```sh
./gradlew :apps:shop:installDebug      # the shop on a connected device
./gradlew :apps:uikit:installDebug     # the UI kit app
```

&nbsp;

## Server

The catalog, promo codes and cart checks come from a small Ktor server in [`server/`](server/README.md). It is deployed at `http://2.56.204.151:8080/`, and both debug and release builds talk to it.

&nbsp;

## Built with AI agents

The app is developed with the AI agents Claude Code, Cursor and Grok Build, on Claude Opus and Grok models. Agents speed the work up a lot, but they don't write good code by themselves: they need rules, step-by-step guidance and checks of what they produce. In this project that works like this:

- **The rules are written down for the agents**: [CLAUDE.md](CLAUDE.md) describes the module layout, the code style and the conventions for screens.
- **Changes go in small steps**, each discussed and checked before it is committed.
- **Behaviour is checked by tests and CI** (see [above](#tests-and-ci)), and changes are also tried on an emulator.
