# Navigation

[Русская версия](../ru/navigation.md) · [All pages](README.md)

Navigation Compose with type-safe routes (`@Serializable` objects and classes). Features don't know each other: each one gives the app a graph, and the app joins the graphs.

&nbsp;

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

> [!TIP]
> App-wide navigation behaviour — what Back does on a tab, how tabs stack — is changed in `AppNavGraph` and the bottom bar, not in the features.

&nbsp;

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

> [!IMPORTANT]
> **`onClose` means "leave the feature", and the host decides what that is.** A tab root has nowhere to go, so the app passes `{}`; the same feature inside a flow would close the flow.

`graph(navController, onClose, …)` is the same everywhere, even where `navController` isn't used yet: a feature can grow screens without changing its signature.

&nbsp;

## Tabs

Each tab keeps its own stack. Going to a tab pops everything, the catalog too, and saves the stack of the tab being left ([`BottomTabs.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/bottombar/BottomTabs.kt)):

```kotlin
navigate(route) {
    popUpTo(graph.id) { saveState = true }  // pop everything, but remember the tab's stack
    launchSingleTop = true
    restoreState = true                     // bring back the stack of the tab we go to
}
```

So Back from any tab root leaves the app instead of going to the catalog.

The bottom bar is shown only inside tabs. Outside them, on the checkout, there is none:

```kotlin
val currentTab = navBackStackEntry?.destination?.bottomTab() ?: return
```

A product image flies between screens within a tab (shared element), but not between tabs: while tabs switch, the shared transition scope isn't passed down.

```kotlin
CompositionLocalProvider(LocalSharedTransitionScope provides this.takeUnless { isSwitchingTabs }) {
    NavHost(/* ... */)
}
```

&nbsp;

## Product details inside the cart tab

Product details belong to the catalog, but a product opened from the cart must stay in the cart tab. For that the catalog gives an interface with the screen's arguments and a function that adds the screen under any route with that interface. The app declares its own route:

```kotlin
@Serializable
data class CartProductRoute(
    override val productId: String,
    override val productName: String? = null,
    override val imageUrl: String? = null,
) : ProductDetailsRoute

catalog.productDetailsScreen<CartProductRoute>(onBack = { navController.popBackStack() })
```

The ViewModel doesn't know which route opened it and reads the arguments by the interface's property names:

```kotlin
private val productId: String = checkNotNull(savedStateHandle[ProductDetailsRoute::productId.name])
```

&nbsp;

## Screen results

The promo code screen returns the applied code to the cart. The cart opens it with a `resultKey`, like a request code, and the app, closing the promo code screen, puts the result into the `savedStateHandle` of the cart's entry under that key:

```kotlin
onCloseWithResult = { resultKey, promoCode ->
    navController.popBackStack<PromoRoutes.Graph>(inclusive = true)
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.set(resultKey, Json.encodeToString(promoCode))
},
```

The cart's navigation reads the result, passes it to the ViewModel as an event and removes it:

```kotlin
LaunchedEffect(promoResult) {
    promoResult?.let { result ->
        viewModel.onEvent(CartEvent.OnPromoCodeApplied(Json.decodeFromString<PromoCode>(result)))
        entry.savedStateHandle.remove<String>(CartResults.PROMO_RESULT_KEY)
    }
}
```

> [!WARNING]
> The result can't go straight into the ViewModel's `SavedStateHandle`: an entry's handle and a ViewModel's handle are separate objects, and the ViewModel would never see it.

&nbsp;

## Navigation goes through the ViewModel

Every button that navigates, Back and Close included, sends an event. The ViewModel answers with an effect, and the screen navigates inside `navigate { }` (see [Screens](screens.md#effects)):

```kotlin
NavigateBackIconButton(onClick = { onEvent(ProductDetailsEvent.OnBackClick) })
// in the ViewModel:  OnBackClick -> send(ProductDetailsEffect.NavigateBack)
// in the screen:     ProductDetailsEffect.NavigateBack -> navigate { onBack() }
```

`navigate { }` runs only while the screen is on top. After the first navigation the screen is no longer on top, so the second one from a fast double tap waits and is dropped:

```kotlin
fun navigate(block: () -> Unit) {
    scope.launch { lifecycle.withResumed(block) }
}
```

<details>
<summary>The second guard: a leaving screen takes no taps</summary>

During a transition the old screen is still visible and used to catch the second tap: a double tap on Back closed two screens. [`BlockTouchesDuringTransitions.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/transitions/BlockTouchesDuringTransitions.kt) drops taps until the top screen is `RESUMED`.

</details>

&nbsp;

## Deep links

The app opens three kinds of links:

| Link | Opens |
|---|---|
| `https://dmitriiecho.github.io/ShoppingApp/catalog` | the catalog tab |
| `https://dmitriiecho.github.io/ShoppingApp/cart` | the cart tab |
| `https://dmitriiecho.github.io/ShoppingApp/product/{id}` | a product over the catalog |

A link opens the way a user would get there ([`DeepLinks.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/DeepLinks.kt)):

```kotlin
// The tab that opens the link; with none, the link is ignored
val tab = BottomNavRoutes.all.firstOrNull { tabGraph(it).hasDeepLink(link) } ?: return
val tabRoot = tabGraph(tab).findStartDestination()

navigateToBottomTab(tab)                       // as a tap on the tab
popBackStack(tabRoot.id, inclusive = false)    // to the tab root
if (!tabRoot.hasDeepLink(link)) navigate(link) // the screen over the root
```

So Back from a product opened by a link goes to the catalog.

- **App Links are verified**: the debug key is kept in the repo, and its fingerprint is in `assetlinks.json` on the domain, so a build from any computer opens the links.
- **The domain and paths are written twice**, in the manifest's intent filter and in `DeepLinkConfig`; a comment in each points to the other.
- **Test pages** with every link, with a trailing slash and edge cases (an unknown product, an unknown path) are in [`docs/deeplinks/`](../../deeplinks/) and published on GitHub Pages; the settings screen opens them.

<details>
<summary>Why the launch link is opened only once</summary>

`MainActivity` opens the link the app was launched with only on the first start and not from Recents (which relaunches the app with the old intent), then clears it so `NavHost` doesn't open it again by itself:

```kotlin
val launchedFromRecents = (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0
if (savedInstanceState == null && !launchedFromRecents) {
    intent.data?.let(viewModel::openDeepLink)
}
intent.data = null
```

</details>
