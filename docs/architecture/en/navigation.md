# Navigation

[Русская версия](../ru/navigation.md) · [All pages](README.md)

Navigation Compose with type-safe routes (`@Serializable` objects and classes). Features don't know each other: each one gives the app a graph, and the app joins the graphs.

<br>
<br>

## How the features are joined

The app builds three tabs and the checkout over them. Every way from one feature to another is a callback that [`AppNavGraph.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppNavGraph.kt) passes to the feature graphs:

| From | To | Callback |
|---|---|---|
| Cart | Product details (inside the cart tab) | `onOpenProduct` |
| Cart | Promo code screen | `onOpenPromo(resultKey)` |
| Promo code screen | back to the cart, with the applied code | `onCloseWithResult(resultKey, promoCode)` |
| Cart | Checkout (over the tabs) | `onOpenCheckout` |

The cart knows neither the promo code screen nor the checkout; it just calls a callback:

```kotlin
cart.cartGraph(
    navController = navController,
    onClose = {}, // Tab root: nothing to close.
    onOpenCheckout = { navController.navigate(CheckoutRoutes.Graph) },
    onOpenPromo = { resultKey -> navController.navigate(PromoRoutes.Graph(resultKey = resultKey)) },
    // ...
)
```

<br>

> [!TIP]
> App-wide navigation behaviour — what Back does on a tab, how tabs stack — is changed in `AppNavGraph` and the bottom bar, not in the features.

<br>
<br>

## A feature's graph

Every feature has the same shape, even one with a single screen:

```kotlin
object CartRoutes {
    @Serializable data object Graph            // public: the app opens the feature with it
    @Serializable internal data object Cart    // the feature's own screens
}

fun CartNavigationScope.graph(
    navController: NavController,
    onClose: () -> Unit,                       // leave the whole feature
    onOpenCheckout: () -> Unit,                // a way out to another feature
    // ...
)
```

<br>

> [!IMPORTANT]
> **`onClose` means "leave the feature", and the host decides what that is.** A tab root has nowhere to go, so the app passes `{}`; the same feature inside a flow would close the flow.

`graph(navController, onClose, …)` is the same everywhere, even where `navController` isn't used yet: a feature can grow screens without changing its signature.

<br>
<br>

## Tabs

<img src="../images/product-image-transition.gif" align="right" width="220" alt="The product image flies from the list to the product details">

**Each tab keeps its own stack.** Switching a tab pops everything, the catalog too ([`BottomTabs.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/bottombar/BottomTabs.kt)), with `saveState`/`restoreState`. So Back from any tab root leaves the app instead of going to the catalog.

**The bottom bar is shown only inside tabs.** The checkout covers it.

**A product image flies between screens** within a tab (shared element), as in the animation on the right. It doesn't fly between tabs: while tabs switch, the shared transition scope is `null`.

<br clear="right">

<br>
<br>

## Product details inside the cart tab

<img src="../images/product-in-cart-tab.png" align="right" width="220" alt="A product opened from the cart: the Cart tab is selected">

Product details belong to the catalog, but a product opened from the cart must stay in the cart tab, as in the screenshot on the right.

The catalog offers two things for that:

- **`ProductDetailsRoute`** — an interface with the screen's arguments: `productId`, `productName`, `imageUrl`;
- **`productDetailsScreen<T>()`** — adds the screen under any route that implements it.

The app declares its own route and adds the screen to the cart tab. The ViewModel reads the arguments by the interface's property names, so it works with any route.

<br clear="right">

```kotlin
@Serializable
data class CartProductRoute(
    override val productId: String,
    override val productName: String? = null,
    override val imageUrl: String? = null,
) : ProductDetailsRoute

catalog.productDetailsScreen<CartProductRoute>(onBack = { navController.popBackStack() })
```

<br>
<br>

## Screen results

The promo code screen returns the applied code to the cart:

1. **The cart opens the promo code screen** with a `resultKey`, like a request code: `onOpenPromo(resultKey)`. So one promo code screen can serve several callers.
2. **The promo code screen closes with a result**, and the app puts the code into the `savedStateHandle` of the cart's back stack entry under that key, as JSON.
3. **The cart's navigation reads the result**, passes it to the ViewModel as an `OnPromoCodeApplied(promoCode)` event and removes it.

<br>

> [!WARNING]
> The result can't go straight into the ViewModel's `SavedStateHandle`: an entry's handle and a ViewModel's handle are separate objects, and the ViewModel would never see it.

<br>
<br>

## Navigation goes through the ViewModel

Every button that navigates, Back and Close included, sends an event. The ViewModel answers with an effect, and the screen navigates inside `navigate { }` (see [Screens](screens.md#effects)):

```kotlin
NavigateBackIconButton(onClick = { onEvent(ProductDetailsEvent.OnBackClick) })
// in the ViewModel:  OnBackClick -> send(ProductDetailsEffect.NavigateBack)
// in the screen:     ProductDetailsEffect.NavigateBack -> navigate { onBack() }
```

<br>

<details>
<summary>How a fast double tap is stopped</summary>

- **`navigate { }` runs only while the screen is on top** (`RESUMED`). After the first navigation the screen is no longer on top, so the second one waits and is dropped when the screen stops.
- **A screen that is leaving doesn't take taps** ([`BlockTouchesDuringTransitions.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/transitions/BlockTouchesDuringTransitions.kt)). During the transition the old screen is still visible and used to catch the second tap: a double tap on Back closed two screens.

</details>

<br>
<br>

## Deep links

| Link | Opens |
|---|---|
| `https://dmitriiecho.github.io/ShoppingApp/catalog` | the catalog tab |
| `https://dmitriiecho.github.io/ShoppingApp/cart` | the cart tab |
| `https://dmitriiecho.github.io/ShoppingApp/product/{id}` | a product over the catalog |

A link opens the way a user would get there ([`DeepLinks.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/DeepLinks.kt)):

1. **Find the tab that opens the link.** If there is none, the link is ignored.
2. **Switch to that tab**, as a tap would, and go back to its root.
3. **Open the screen over the root.** So Back from a product opened by a link goes to the catalog.

More about links:

- **App Links are verified**: the debug key is kept in the repo, and its fingerprint is in `assetlinks.json` on the domain, so a build from any computer opens the links.
- **The domain and paths are written twice**, in the manifest's intent filter and in `DeepLinkConfig`; a comment in each points to the other.
- **Test pages** with every link, with a trailing slash and edge cases (an unknown product, an unknown path) are in [`docs/deeplinks/`](../../deeplinks/) and published on GitHub Pages; the settings screen opens them.

<br>

<details>
<summary>Why the launch link is opened only once</summary>

`MainActivity` opens the link the app was launched with, then clears it from the intent: otherwise `NavHost` would open it again by itself, with a different back stack. The link isn't opened again after recreation (the restored screens already show it) or from Recents, which relaunches the app with the old intent.

</details>
