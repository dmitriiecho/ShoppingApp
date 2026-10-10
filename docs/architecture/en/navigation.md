# Navigation

[Русская версия](../ru/navigation.md) · [All pages](README.md)

Navigation is built on Navigation Compose with type-safe routes (`@Serializable` objects and classes). Features don't know about each other: each one gives the app a graph, and the app connects the graphs.

&nbsp;

## How features are connected

The app builds three tabs, with checkout shown on top of them. Every path from one feature to another is a callback that [`AppNavGraph.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppNavGraph.kt) passes to the feature graphs:

| From | To | Callback |
|---|---|---|
| Cart | Product details (inside the cart tab) | `onOpenProduct` |
| Cart | Promo code screen | `onOpenPromo(resultKey)` |
| Promo code screen | back to the cart, with the applied code | `onCloseWithResult(resultKey, promoCode)` |
| Cart | Checkout (on top of the tabs) | `onOpenCheckout` |

The cart knows about neither the promo code screen nor checkout; it just calls a callback:

```kotlin
cart.cartGraph(
    navController = navController,
    onClose = {}, // Tab root: nothing to close.
    onOpenCheckout = { navController.navigate(CheckoutRoutes.Graph) },
    onOpenPromo = { resultKey -> navController.navigate(PromoRoutes.Graph(resultKey = resultKey)) },
    // ...
)
```

> [!TIP]
> App-wide navigation behaviour, such as what Back does on a tab or how tabs keep their stacks, is changed in `AppNavGraph` and the bottom bar, not in the features.

&nbsp;

## A feature's graph

Every feature follows the same shape, even one with a single screen:

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

> [!IMPORTANT]
> **`onClose` means "leave the feature", and the host decides what that means.** A tab root has nowhere to go back to, so the app passes `{}`; inside a flow, the same feature would close the flow.

The `graph(navController, onClose, …)` signature is the same everywhere, even where `navController` isn't used yet, so a feature can add screens without changing it.

&nbsp;

## Tabs

Each tab keeps its own back stack. Switching to a tab pops everything, including the catalog, and saves the stack of the tab you're leaving ([`BottomTabs.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/bottombar/BottomTabs.kt)):

```kotlin
navigate(route) {
    popUpTo(graph.id) { saveState = true }  // pop everything, but remember the tab's stack
    launchSingleTop = true
    restoreState = true                     // bring back the stack of the tab we go to
}
```

As a result, Back from any tab root leaves the app instead of going to the catalog.

The bottom bar is shown only inside tabs; checkout sits outside them, so it has none:

```kotlin
val currentTab = navBackStackEntry?.destination?.bottomTab() ?: return
```

The product image flies between screens within a tab (a shared element), but not between tabs: while tabs are switching, the shared transition scope isn't passed down.

```kotlin
CompositionLocalProvider(LocalSharedTransitionScope provides this.takeUnless { isSwitchingTabs }) {
    NavHost(/* ... */)
}
```

&nbsp;

## Product details inside the cart tab

Product details belong to the catalog, but a product opened from the cart has to stay in the cart tab. To make that possible, the catalog provides an interface with the screen's arguments and a function that adds the screen under any route implementing that interface. The app declares its own route:

```kotlin
@Serializable
data class CartProductRoute(
    override val productId: String,
    override val productName: String? = null,
    override val imageUrl: String? = null,
) : ProductDetailsRoute

catalog.productDetailsScreen<CartProductRoute>(onBack = { navController.popBackStack() })
```

The ViewModel doesn't know which route opened it; it reads the arguments using the interface's property names:

```kotlin
private val productId: String = checkNotNull(savedStateHandle[ProductDetailsRoute::productId.name])
```

&nbsp;

## Screen results

The promo code screen returns the applied code to the cart. The cart opens it with a `resultKey`, much like a request code, and when the promo code screen closes, the app puts the result into the `savedStateHandle` of the cart's back stack entry under that key:

```kotlin
onCloseWithResult = { resultKey, promoCode ->
    navController.popBackStack<PromoRoutes.Graph>(inclusive = true)
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.set(resultKey, Json.encodeToString(promoCode))
},
```

The cart's navigation code reads the result, passes it to the ViewModel as an event and then removes it:

```kotlin
LaunchedEffect(promoResult) {
    promoResult?.let { result ->
        viewModel.onEvent(CartEvent.OnPromoCodeApplied(Json.decodeFromString<PromoCode>(result)))
        entry.savedStateHandle.remove<String>(CartResults.PROMO_RESULT_KEY)
    }
}
```

> [!WARNING]
> The result can't be put straight into the ViewModel's `SavedStateHandle`: a back stack entry's handle and a ViewModel's handle are separate objects, so the ViewModel would never see it.

&nbsp;

## Navigation goes through the ViewModel

Every button that navigates, including Back and Close, sends an event. The ViewModel responds with an effect, and the screen navigates inside `navigate { }` (see [Screens](screens.md#effects)):

```kotlin
NavigateBackIconButton(onClick = { onEvent(ProductDetailsEvent.OnBackClick) })
// in the ViewModel:  OnBackClick -> send(ProductDetailsEffect.NavigateBack)
// in the screen:     ProductDetailsEffect.NavigateBack -> navigate { onBack() }
```

`navigate { }` runs only while the screen is on top. After the first navigation the screen is no longer on top, so a second navigation from a quick double tap waits and is then dropped:

```kotlin
fun navigate(block: () -> Unit) {
    scope.launch { lifecycle.withResumed(block) }
}
```

<details>
<summary>A second safeguard: a screen that is leaving ignores taps</summary>

During a transition the old screen is still visible, and it used to catch the second tap: a double tap on Back closed two screens. [`BlockTouchesDuringTransitions.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/transitions/BlockTouchesDuringTransitions.kt) ignores taps until the top screen is `RESUMED`.

</details>

&nbsp;

## Deep links

The app handles three kinds of links:

| Link | Opens |
|---|---|
| `https://dmitriiecho.github.io/ShoppingApp/catalog` | the catalog tab |
| `https://dmitriiecho.github.io/ShoppingApp/cart` | the cart tab |
| `https://dmitriiecho.github.io/ShoppingApp/product/{id}` | a product on top of the catalog |

A link opens a screen the same way a user would get there ([`DeepLinks.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/DeepLinks.kt)):

```kotlin
// The tab that handles the link; if there is none, the link is ignored
val tab = BottomNavRoutes.all.firstOrNull { tabGraph(it).hasDeepLink(link) } ?: return
val tabRoot = tabGraph(tab).findStartDestination()

navigateToBottomTab(tab)                       // like tapping the tab
popBackStack(tabRoot.id, inclusive = false)    // back to the tab root
if (!tabRoot.hasDeepLink(link)) navigate(link) // the screen on top of the root
```

So Back from a product opened by a link goes to the catalog.

- **App Links are verified**: the debug key is kept in the repo and its fingerprint is listed in `assetlinks.json` on the domain, so a build from any computer can open the links.
- **The domain and paths are declared twice**, in the manifest's intent filter and in `DeepLinkConfig`; a comment in each one points to the other.
- **Test pages** with every link, including variants with a trailing slash and edge cases (an unknown product, an unknown path), are in [`docs/deeplinks/`](../../deeplinks/) and published on GitHub Pages; the Settings screen opens them.

<details>
<summary>Why the launch link is opened only once</summary>

`MainActivity` opens the link the app was launched with only on the first start, not when the app is relaunched from Recents (which reuses the old intent). Then it clears the link so that `NavHost` doesn't open it again on its own:

```kotlin
val launchedFromRecents = (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0
if (savedInstanceState == null && !launchedFromRecents) {
    intent.data?.let(viewModel::openDeepLink)
}
intent.data = null
```

</details>
