# Screens

[Русская версия](../ru/screens.md) · [All pages](README.md)

A screen has one UI state, a list of events from the user and a list of one-off effects. The ViewModel turns events into a new state or an effect; the screen only draws the state and reacts to effects.

&nbsp;

## The files of a screen

Every screen is built the same way. Taking the cart as an example (`feature/cart/impl/.../presentation/cart/`):

| File | Holds |
|---|---|
| `CartScreen.kt` | two `CartScreen` overloads, the private `CartContent` and small helpers such as the top bar |
| `CartViewModel.kt` | the state, event handling, effects |
| `CartUiState.kt` | the state and its nested types |
| `CartEvent.kt`, `CartEffect.kt` | what the user does and what happens once |
| `components/` | sections with their own previews: `CartItemCard`, `CartTotalsCard`, … |

&nbsp;

## Two screen overloads

Instead of a Route + Screen pair, a screen has two functions with the same name. The first takes the ViewModel and collects the state and the effects, the second only draws:

```kotlin
@Composable
internal fun CartScreen(
    onBack: () -> Unit,
    onOpenCheckout: () -> Unit,
    // ...
    viewModel: CartViewModel = hiltViewModel(),
)

@Composable
internal fun CartScreen(
    uiState: CartUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CartEvent) -> Unit,
    modifier: Modifier = Modifier,
)
```

The second one doesn't know Hilt, so it can be previewed in any state.

`onEvent` goes no further than this file: the screen, `CartContent` and tiny private helpers. Sections in `components/` get specific callbacks, so they don't know the screen's events and can be previewed on their own:

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

The state is one `StateFlow` per screen: `combine` over the sources and `stateIn`. Types that only make up the state are nested in it:

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

- **Nested types are written through their owner**: `CartUiState.Item`; inside the owner the name isn't repeated: `PromoCodeUiState.Check`, not `PromoCodeCheck`.
- **Fields are grouped by screen section**, not listed flat: checkout has `order`, `address`, `paymentMethod`.
- **Loading is a state, not a flag or `null`**: sealed `Loading` / `Loaded` / `Error` with these names everywhere. `null` only means "unknown", such as a product name a deep link didn't pass.
- **The state holds what the screen draws.** A domain model the screen shows as is goes in as is: checkout's order items, the promo codes in the hint. A screen gets its own UI model on plain values when it needs ready decisions or [Compose stability](#compose-stability): `CartUiState.Item` holds `canAddOneMore` and the item's issues, so "+" redraws only its own card.
- **Repeated conditions are getters** on the state, like `canCheckout` above.

A check before an action reads the source itself, not `uiState`: the state gets a change only after `combine`, a moment later. Otherwise a fast double tap on "Place order" placed the order twice:

```kotlin
private fun submitOrder() {
    if (isSubmitting.value || !uiState.value.canSubmit) return
    isSubmitting.value = true
    // ...
}
```

&nbsp;

## Effects

Navigation and snackbars happen once, so they are effects sent through a `Channel`, not part of the state; a state would need one more event to say "handled". A screen handles them with [`ObserveEffects`](../../../core/compose-utils/src/main/java/krio/systemdesign/shoppingapp/core/composeutils/effects/ObserveEffects.kt):

```kotlin
ObserveEffects(viewModel.effects) { effect ->
    when (effect) {
        CartEffect.NavigateToCheckout -> navigate { onOpenCheckout() }
        is CartEffect.ShowSnackBar -> showSnackbar(snackbarHostState, effect.message.asString(resources))
        // ...
    }
}
```

Inside `ObserveEffects` the effects are collected while the screen is visible and handled at once, on `Main.immediate`:

```kotlin
lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
    withContext(Dispatchers.Main.immediate) {
        effects.collect { effect -> scope.currentOnEffect(effect) }
    }
}
```

- **An effect sent in the background** waits in the channel and is handled when the user comes back.
- **`Main.immediate`** makes the effect be handled right inside the ViewModel's `send`: with a pause in between, a screen stopping at that moment would take the effect and lose it.
- **`navigate { }` runs only while the screen is on top**, and **`showSnackbar` runs in its own coroutine**: a snackbar waits until it is dismissed and would hold the next effect.

A screen has one snackbar effect, `ShowSnackBar(UiText)`: the ViewModel builds the text from a string resource without access to resources.

&nbsp;

## Text fields

A field's text is a `TextFieldState` created in the ViewModel and put into the state as the same instance for the whole screen. `savedTextField` keeps it in `SavedStateHandle`, so the text survives process death:

```kotlin
private val searchQuery: TextFieldState = savedStateHandle.savedTextField(KEY_SEARCH_QUERY)
```

- **No `OnTextChange` event.** The field edits the state in place, so typing never waits for a flow and can't lose characters.
- **Values derived from the text are getters** on the state. Compose tracks the read:

  ```kotlin
  val canApply: Boolean
      get() = promoCode.text.isNotBlank() && !isChecking
  ```

- **`snapshotFlow { state.text }` in the ViewModel is for actions**: search, clearing an error after an edit.

&nbsp;

## Saved state

What the user typed or opened survives process death; what a request was doing doesn't:

| Survives | How |
|---|---|
| text in fields | `savedStateHandle.savedTextField(key)` |
| an open dialog, a chosen option | `savedStateHandle.getStateFlow(key, default)` |
| the catalog page in view | the page number in `SavedStateHandle`; the list reopens there |
| results of requests | not kept: the screen loads again |

&nbsp;

## Compose stability

Strong skipping is on, so by default nothing is annotated: a composable skips when it gets the same instance again. Only what compiler reports and a recomposition log show gets fixed.

To make an object change only when its source does, the source is mapped before `combine`:

```kotlin
val uiState = combine(
    observeCart().map { it.toOrder() },  // a new order only when the cart changes
    paymentMethod,
    isSubmitting,
) { order, payment, submitting -> CheckoutUiState(/* ... */) }
```

Models from `:shared:domain` are unstable for Compose: that module has no Compose compiler, and no Compose annotations go there. That matters for a list whose cards change one by one, like the cart: there the screen gets its own UI model with plain values, `CartUiState.Item`. A screen that shows the data as is, like checkout, keeps the domain models.

List callbacks take the item's id instead of capturing the item. So every card gets the same lambdas, and "+" redraws only its own card:

```kotlin
onQuantityChange: (productId: String, quantity: Int) -> Unit
```

&nbsp;

## Wide screens

Phones show the app upright only, but Android 16+ rotates it anyway on screens 600 dp and wider: tablets, unfolded foldables, desktop windows. So a screen lays out for the width:

- **Content stays a column** of `ContentMaxWidth` (600 dp) in the middle, with `Modifier.contentWidth()` from `:core:designsystem`. It goes inside the scrolling container, so the screen also scrolls beside the column.
- **The product screen held sideways** puts the image and the details side by side: below a square image the details would start off the screen.
- **Landscape on a phone** isn't laid out yet: on the low screen the bottom bars of the cart and checkout leave little room for the content.

&nbsp;

## Components and previews

A screen is built from ready components: every styled element comes from `:core:designsystem`, `:shared:ui` or the feature's `ui` module (see [Modules](modules.md#where-new-code-goes)). The screen doesn't style Material components itself.

Previews sit next to what they preview, private, in the light and dark themes. A screen file previews states that look different as a whole screen; a section's states are previewed in the section's file. Every preview is also a [screenshot test](testing.md#screenshots), so a state that has a preview is guarded against changes nobody asked for.
