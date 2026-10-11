# Navigation

[Русская версия](../ru/navigation.md) · [All pages](README.md)

Navigation is built on Navigation 3: a screen is a `@Serializable` key, a back stack is a list of keys that the app owns, and `NavDisplay` shows its top. Features don't know about each other: each one gives the app its screens, and the app connects them.

&nbsp;

## How features are connected

The app builds three tabs, with checkout shown on top of them. Every path from one feature to another is a callback that [`AppEntries.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppEntries.kt) passes to the features:

| From | To | Callback |
|---|---|---|
| Cart | Product details (inside the cart tab) | `onOpenProduct` |
| Cart | Promo code screen | `onOpenPromo(resultKey)` |
| Promo code screen | back to the cart, with the applied code | `onCloseWithResult(resultKey, promoCode)` |
| Cart | Checkout (on top of the tabs) | `onOpenCheckout` |

The cart knows about neither the promo code screen nor checkout; it just calls a callback:

```kotlin
cart.entries(
    navigator = navigator,
    onClose = {}, // Tab root: nothing to close.
    onOpenCheckout = { navigator.openOverTabs(CheckoutRoutes.Checkout) },
    onOpenPromo = { resultKey -> navigator.navigate(PromoRoutes.PromoCode(resultKey)) },
    // ...
)
```

> [!TIP]
> App-wide navigation behaviour, such as what Back does on a tab or how tabs keep their stacks, is changed in [`AppNavigator.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppNavigator.kt), not in the features.

&nbsp;

## A feature's screens

Every feature follows the same shape, even one with a single screen:

```kotlin
object CartRoutes {
    @Serializable data object Cart : NavKey    // public: the app opens the feature with its first screen
}

fun CartNavigationScope.entries(
    navigator: Navigator,                      // moves between the feature's own screens
    onClose: () -> Unit,                       // leave the whole feature
    onOpenCheckout: () -> Unit,                // a way out to another feature
    // ...
)

val cartNavKeys: SerializersModule             // the feature's keys, so the app can save the back stack
```

> [!IMPORTANT]
> **`onClose` means "leave the feature", and the host decides what that means.** A tab root has nowhere to go back to, so the app passes `{}`; the promo code screen is closed together with everything it opened (`navigator.popUpTo(PromoRoutes.PromoCode::class)`), checkout by closing what's over the tabs.

The `entries(navigator, onClose, …)` signature is the same everywhere, even where `navigator` isn't used yet, so a feature can add screens without changing it. `Navigator` ([`:core:compose-utils`](../../../core/compose-utils/src/main/java/krio/systemdesign/shoppingapp/core/composeutils/navigation/Navigator.kt)) only opens a screen and goes back; which back stack the screen goes to is the app's decision.

&nbsp;

## Back stacks and tabs

What the user has open is [`NavigationState`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/NavigationState.kt): the current tab, a back stack for each tab, and a back stack over the tabs, for checkout. It survives process death: every feature registers its keys (`cartNavKeys` and the like), and the app saves them with kotlinx.serialization, which would work on any platform. Only [`AppNavigator`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppNavigator.kt) changes it:

```kotlin
override fun navigate(key: NavKey) {
    activeStack.add(key)            // over the tabs while something is open there, otherwise the current tab
}

fun switchTab(tab: BottomTab) {
    state.overTabs.clear()
    state.currentTab = tab          // the tab left keeps its stack
}
```

`NavDisplay` gets the screens of the current tab followed by the screens over the tabs. Each back stack keeps its own saved state and ViewModels, so a tab the user left keeps its screens as they were.

Back from a tab root leaves the app instead of going to the catalog: with one screen left, `NavDisplay` doesn't handle Back, and the system closes the app.

The bottom bar is shown only while nothing is over the tabs, so checkout has none.

The product image flies between screens within a tab (a shared element), but not between tabs. The screens it flies on are marked with `sharedElementScreen()`, and the app gives them the animation of their entry. While tabs are switching, screens of two tabs are on screen at once, and then the shared transition scope isn't passed down:

```kotlin
val isSwitchingTabs = tabsOnScreen.distinct().size > 1
CompositionLocalProvider(LocalSharedTransitionScope provides this.takeUnless { isSwitchingTabs }) {
    NavDisplay(/* ... */)
}
```

&nbsp;

## Product details inside the cart tab

Product details belong to the catalog, but a product opened from the cart has to stay in the cart tab. To make that possible, the catalog provides an interface with the screen's arguments and a function that adds the screen under any key implementing that interface. The app declares its own key:

```kotlin
@Serializable
data class CartProductRoute(
    override val productId: String,
    override val productName: String? = null,
    override val imageUrl: String? = null,
) : ProductDetailsRoute, NavKey

catalog.productDetailsEntry<CartProductRoute>(onBack = navigator::goBack)
```

The ViewModel doesn't know which key opened it. Its key's fields are put into its `SavedStateHandle` (`injectedViewModel(arguments = key, …)`, as Navigation 2 did with route arguments), and it reads them using the interface's property names:

```kotlin
private val productId: String = checkNotNull(savedStateHandle[ProductDetailsRoute::productId.name])
```

&nbsp;

## Screen results

The promo code screen returns the applied code to the cart through Navigation 3's `ResultEventBus`. The cart opens it with a `resultKey`, much like a request code, and when the promo code screen closes with a code, the app sends it under that key:

```kotlin
onCloseWithResult = { resultKey, promoCode ->
    resultEventBus.sendResult(resultKey, promoCode)
    navigator.popUpTo(PromoRoutes.PromoCode::class)
},
```

The cart's navigation code receives the result, passes it to the ViewModel as an event and then removes it, so it isn't applied again:

```kotlin
ResultEffect<PromoCode>(CartResults.PROMO_RESULT_KEY) { promoCode ->
    viewModel.onEvent(CartEvent.OnPromoCodeApplied(promoCode))
    resultEventBus.removeResult(CartResults.PROMO_RESULT_KEY)
}
```

> [!NOTE]
> The bus lives in memory, so a result sent but not yet received is lost if the process dies in between. Once received, the code is saved by the ViewModel, so the cart has it after process death.

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

During a transition the old screen is still visible, and it used to catch the second tap: a double tap on Back closed two screens. `NavDisplay` keeps a screen below `RESUMED` until its transition ends, and [`blockTouchesUntilResumed`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/transitions/EntryDecorators.kt), a decorator on every entry, ignores taps until then.

</details>

&nbsp;

## Deep links

The app handles three kinds of links:

| Link | Opens |
|---|---|
| `https://dmitriiecho.github.io/ShoppingApp/catalog` | the catalog tab |
| `https://dmitriiecho.github.io/ShoppingApp/cart` | the cart tab |
| `https://dmitriiecho.github.io/ShoppingApp/product/{id}` | a product on top of the catalog |

Each feature declares the links it opens as Navigation 3 matchers (`catalogDeepLinks`, `cartDeepLinks`), and the app knows which tab each feature's links open in. A link opens a screen the same way a user would get there ([`DeepLinks.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/DeepLinks.kt), [`AppNavigator.kt`](../../../apps/shop/src/main/java/krio/systemdesign/shoppingapp/navigation/AppNavigator.kt)):

```kotlin
switchTab(tab)                            // like tapping the tab
stack.subList(1, stack.size).clear()      // back to the tab root
if (key != tab.root) stack.add(key)       // the screen on top of the root
```

So Back from a product opened by a link goes to the catalog. A link only sets `productId`: the product's name and image come from the server, not from the link.

- **App Links are verified**: the debug key is kept in the repo and its fingerprint is listed in `assetlinks.json` on the domain, so a build from any computer can open the links.
- **The domain and paths are declared twice**, in the manifest's intent filter and in `DeepLinkConfig`; a comment in each one points to the other.
- **Test pages** with every link, including variants with a trailing slash and edge cases (an unknown product, an unknown path), are in [`docs/deeplinks/`](../../deeplinks/) and published on GitHub Pages; the Settings screen opens them.

<details>
<summary>Why the launch link is opened only once</summary>

`MainActivity` opens the link the app was launched with only on the first start, not when the app is relaunched from Recents (which reuses the old intent):

```kotlin
val launchedFromRecents = (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0
if (savedInstanceState == null && !launchedFromRecents) {
    intent.data?.let(viewModel::openDeepLink)
}
```

</details>
