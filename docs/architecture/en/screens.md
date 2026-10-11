# Screens

[Русская версия](../ru/screens.md) · [All pages](README.md)

Each screen has one UI state, a set of user events and a set of one-off effects. The ViewModel turns events into a new state or an effect; the screen only draws the state and reacts to effects.

&nbsp;

## How a screen is split into files

Every screen is built the same way. Here is the cart as an example (`feature/cart/impl/.../presentation/cart/`):

| File | Holds |
|---|---|
| `CartScreen.kt` | two `CartScreen` overloads, the private `CartContent` and small helpers such as the top bar |
| `CartViewModel.kt` | the state, event handling, effects |
| `CartUiState.kt` | the state and its nested types |
| `CartEvent.kt`, `CartEffect.kt` | what the user does, and what happens only once |
| `components/` | sections with their own previews: `CartItemCard`, `CartTotalsCard`, … |

&nbsp;

## Two screen overloads

A screen has two functions with the same name. The first takes the ViewModel and collects the state and the effects, the second only draws:

```kotlin
@Composable
internal fun CartScreen(
    onBack: () -> Unit,
    onOpenCheckout: () -> Unit,
    // ...
    viewModel: CartViewModel = injectedViewModel(),
)

@Composable
internal fun CartScreen(
    uiState: CartUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CartEvent) -> Unit,
    modifier: Modifier = Modifier,
)
```

The second one doesn't depend on DI, so it can be previewed in any state.

`injectedViewModel()` takes the ViewModel of the screen's back stack entry and creates it, the first time, with the app's `InjectedViewModelFactory`. The factory builds a small Metro graph for each new ViewModel, `ViewModelGraph`, with the ViewModel's own `SavedStateHandle` (as Hilt's `ViewModelComponent` does), so a ViewModel takes the handle in its constructor like any other dependency. A ViewModel joins that graph with two annotations:

```kotlin
@ViewModelKey
@ContributesIntoMap(ViewModelScope::class)
internal class CartViewModel(
    private val observeCart: ObserveCartUseCase,
    // ...
    private val savedStateHandle: SavedStateHandle,
) : ViewModel()
```

`onEvent` stays within this file: the screen, `CartContent` and small private helpers. Sections in `components/` get specific callbacks instead, so they don't know about the screen's events and can be previewed on their own:

```kotlin
CartItemCard(
    item = item,
    onQuantityChange = { productId, quantity -> onEvent(CartEvent.OnQuantityChange(productId, quantity)) },
    onRemove = { onEvent(CartEvent.OnRemoveFromCartClick(it)) },
    // ...
)
```

&nbsp;

## UI state

The state is a single `StateFlow` per screen, built with `combine` over its sources and `stateIn`. Types that exist only as part of the state are nested inside it:

```kotlin
data class CartUiState(
    val content: Content = Content.Loading,
    val isOpeningCheckout: Boolean = false,
    val isClearCartDialogVisible: Boolean = false,
) {
    val canCheckout: Boolean
        get() = content is Content.Loaded && content.allowsCheckout && !isOpeningCheckout

    sealed interface Content {
        data object Loading : Content
        data class Loaded(val items: List<Item>, /* ... */) : Content
    }
}
```

- **Nested types are referred to through their owner**, as in `CartUiState.Item`, and their names don't repeat the owner's: `PromoCodeUiState.Check`, not `PromoCodeCheck`.
- **Fields are grouped by screen section** rather than listed flat: checkout has `order`, `address` and `paymentMethod`.
- **Loading is a state, not a flag or `null`**: a sealed `Loading` / `Loaded` / `Error`, with these names everywhere. `null` only means "unknown", for example a product name that a deep link didn't pass.
- **The state holds what the screen draws.** If the screen shows a domain model as is, the model goes into the state as is: checkout's order items, the promo codes in the hint. A screen gets its own UI model with plain values when it needs precomputed decisions or [Compose stability](#compose-stability): `CartUiState.Item` holds `canAddOneMore` and the item's issues, so tapping "+" redraws only that item's card.
- **Conditions used in several places are getters** on the state, like `canCheckout` above.

A check before an action reads the source directly, not `uiState`: the state only gets the change after `combine`, a moment later. Before this, a quick double tap on "Place order" placed the order twice:

```kotlin
private fun submitOrder() {
    if (submission.value is Submission.Submitting || !uiState.value.canSubmit) return
    val order = uiState.value.order as? CheckoutUiState.Order.Loaded ?: return
    submission.value = Submission.Submitting(order)
    // ...
}
```

&nbsp;

## Effects

Navigation and snackbars happen once, so they are effects sent through a `Channel` rather than part of the state; in the state they would need one more event to mark them as handled. A screen handles them with [`ObserveEffects`](../../../core/compose-utils/src/main/java/krio/systemdesign/shoppingapp/core/composeutils/effects/ObserveEffects.kt):

```kotlin
ObserveEffects(viewModel.effects) { effect ->
    when (effect) {
        CartEffect.NavigateToCheckout -> navigate { onOpenCheckout() }
        is CartEffect.ShowSnackBar -> showSnackbar(snackbarHostState, effect.message.asString(resources))
        // ...
    }
}
```

Inside `ObserveEffects`, effects are collected while the screen is visible and handled immediately, on `Main.immediate`:

```kotlin
lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
    withContext(Dispatchers.Main.immediate) {
        effects.collect { effect -> scope.currentOnEffect(effect) }
    }
}
```

- **An effect sent while the app is in the background** waits in the channel and is handled when the user comes back.
- **`Main.immediate`** makes the screen handle the effect right inside the ViewModel's `send`: with a pause in between, a screen stopping at that moment could receive the effect and lose it.
- **`navigate { }` runs only while the screen is on top**, and **`showSnackbar` runs in its own coroutine**, because a snackbar suspends until it is dismissed and would otherwise hold up the next effect.

A screen has a single snackbar effect, `ShowSnackBar(UiText)`, so the ViewModel can build the text from a string resource without having access to resources.

&nbsp;

## Text fields

A field's text is a `TextFieldState` created in the ViewModel and put into the state; the same instance lives as long as the screen. `savedTextField` stores it in `SavedStateHandle`, so the text survives process death:

```kotlin
private val searchQuery: TextFieldState = savedStateHandle.savedTextField(KEY_SEARCH_QUERY)
```

- **There is no `OnTextChange` event.** The field edits the state directly, so typing never waits for a flow and can't drop characters.
- **Values derived from the text are getters** on the state, and Compose tracks the reads:

  ```kotlin
  val canApply: Boolean
      get() = promoCode.text.isNotBlank() && !isChecking
  ```

- **`snapshotFlow { state.text }` in the ViewModel is used for actions**, such as running a search or clearing an error after the user edits the text.

&nbsp;

## Saved state

What the user typed or opened survives process death; the progress of requests doesn't:

| Survives | How |
|---|---|
| text in fields | `savedStateHandle.savedTextField(key)` |
| an open dialog, a chosen option | `savedStateHandle.getStateFlow(key, default)` |
| the visible catalog page | the page number in `SavedStateHandle`; the list reopens at that page |
| results of requests | not kept: the screen loads them again |

&nbsp;

## Compose stability

Strong skipping is enabled, so by default nothing is annotated: a composable is skipped when it receives the same instance again. Only problems that compiler reports and recomposition logs reveal get fixed.

To make an object change only when its source changes, the source is mapped before `combine`:

```kotlin
val uiState = combine(
    observeCart().map { it.toOrder() },  // a new order only when the cart changes
    paymentMethod,
    submission,
) { cartOrder, payment, latestSubmission -> CheckoutUiState(/* ... */) }
```

Models from `:shared:domain` are unstable for Compose: that module doesn't use the Compose compiler, and Compose annotations don't belong there. This matters for lists whose items change one at a time, like the cart: there the screen gets its own UI model with plain values, `CartUiState.Item`. A screen that shows the data as is, like checkout, keeps the domain models.

List callbacks take the item's id instead of capturing the item. That way every card gets the same lambdas, and tapping "+" redraws only that card:

```kotlin
onQuantityChange: (productId: String, quantity: Int) -> Unit
```

&nbsp;

## Wide screens

Phones show the app in portrait only, by design. Android 16 and later rotates it anyway on screens 600 dp and wider: tablets, unfolded foldables and desktop windows. So screens adapt their layout to the width:

- **Content stays in a column** of `ContentMaxWidth` (600 dp) in the middle of the screen, using `Modifier.contentWidth()` from `:core:designsystem`. The modifier goes inside the scrolling container, so the screen can also be scrolled from the empty space beside the column.
- **The top bar lines up with the column** using `Modifier.contentBarWidth()`: it is as wide as the column plus the screen padding on each side, so the title and buttons sit above the edges of the content, just as on a phone.
- **In landscape, the product screen** puts the image and the details side by side: under a square image, the details would start below the bottom of the screen.

&nbsp;

<p align="center">
  <img src="../media/tablet.webp" width="720" alt="A tablet in landscape: a product from the catalog, two in the cart, a promo code in the cart and checkout scrolled down to the totals">
</p>

&nbsp;

## Components and previews

A screen is assembled from ready-made components: every styled element comes from `:core:designsystem`, `:shared:ui` or the feature's `ui` module (see [Modules](modules.md#where-new-code-goes)). The screen doesn't style Material components itself.

Previews live next to the code they show, are private, and come in light and dark themes. A screen file previews the states that look different at the whole-screen level; a section's states are previewed in the section's file. Every preview is also a [screenshot test](testing.md#screenshots), so any state with a preview is protected from unintended changes.
