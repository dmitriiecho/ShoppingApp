# Screens

[Русская версия](../ru/screens.md) · [All pages](README.md)

A screen has one UI state, a list of events from the user and a list of one-off effects. The ViewModel turns events into a new state or an effect; the screen only draws the state and reacts to effects.

## The files of a screen

Taking the cart as an example (`feature/cart/impl/.../presentation/cart/`):

| File | Holds |
|---|---|
| `CartScreen.kt` | two `CartScreen` overloads, the private `CartContent` and small helpers such as the top bar |
| `CartViewModel.kt` | the state, event handling, effects |
| `CartUiState.kt` | the state and its nested types |
| `CartEvent.kt`, `CartEffect.kt` | what the user does and what happens once |
| `components/` | sections with their own previews (`CartItemCard`, `CartTotalsCard`, …), `internal` |

### Two overloads instead of Route + Screen

- **The public `CartScreen(onBack, …, viewModel = hiltViewModel())`** collects the state and the effects.
- **The `internal CartScreen(uiState, snackbarHostState, onEvent, modifier)`** is stateless and holds the `Scaffold`, so it can be previewed without Hilt.

The navigation code of other features calls the screen by one name either way.

### Events go only through the screen file

`onEvent` reaches the stateless screen, `CartContent` and tiny private helpers. Sections in `components/` get specific callbacks (`onRetry`, `onQuantityChange`), so they don't know the screen's events and can be previewed on their own.

## UI state

```kotlin
data class CartUiState(
    val content: Content = Content.Loading,
    val isOpeningCheckout: Boolean = false,
    val isClearCartDialogVisible: Boolean = false,
) {
    sealed interface Content {
        data object Loading : Content
        data class Loaded(val items: List<Item>, /* … */) : Content
    }
    // ...
}
```

- **One `StateFlow` per screen**, built with `combine` over the sources and `stateIn(WhileSubscribed(5_000))`.
- **Nested types.** Types that only make up the state live inside `XxxUiState` and are written as `CartUiState.Item`. Inside the owner the name isn't repeated: `PromoCodeUiState.Check`, not `PromoCodeCheck`.
- **Fields are grouped by screen section**, not listed flat: checkout has `order`, `address`, `paymentMethod`.
- **Loading is a state, not a flag or `null`**: sealed `Loading` / `Loaded` / `Error` with these names everywhere. `null` only means "unknown", such as a product name a deep link didn't pass.
- **The state holds what the screen draws**, decisions included (`canAddOneMore`), not domain models or raw data.
- **Repeated conditions are getters** on the state: `canCheckout`, `showsOrder`.

### Actions read the sources, not the state

`uiState` gets a change only after `combine`, a moment later. A check before an action reads the source flow itself: checkout's "Place order" checks `isSubmitting.value`, otherwise a fast double tap placed the order twice.

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

- **Collected while the screen is visible** (`STARTED`). An effect sent in the background waits in the channel and is handled when the user comes back.
- **On `Main.immediate`.** The effect is handled right inside the ViewModel's `send`; with a pause in between, a screen stopping at that moment would take the effect and lose it.
- **`navigate { }` runs only while the screen is on top** (`RESUMED`), which stops a double tap from navigating twice.
- **`showSnackbar` runs in its own coroutine**: a snackbar waits until it is dismissed and would hold the next effect.
- **One snackbar effect per screen**, `ShowSnackBar(UiText)`: the ViewModel builds the text from a string resource without access to resources.

## Text fields

A field's text is a `TextFieldState` created in the ViewModel and put into the state as the same instance for the whole screen:

```kotlin
private val searchQuery: TextFieldState = savedStateHandle.savedTextField(KEY_SEARCH_QUERY)
```

- **No `OnTextChange` event.** The field edits the state in place, so typing never waits for a flow and can't lose characters: the old `value` + `onValueChange` round trip lost them on fast typing.
- **Values derived from the text are getters** on the state (`canApply`). Compose tracks the read.
- **`snapshotFlow { state.text }` in the ViewModel is for actions**: search, clearing an error after an edit.

## Saved state

What the user typed or opened survives process death; what a request was doing doesn't.

| Survives | How |
|---|---|
| text in fields | `savedStateHandle.savedTextField(key)` |
| an open dialog, a chosen option | `savedStateHandle.getStateFlow(key, default)` |
| the catalog page in view | the page number in `SavedStateHandle`; the list reopens there |
| results of requests | not kept: the screen loads again |

## Compose stability

Strong skipping is on, so by default nothing is annotated:

- **No `@Stable`, `@Immutable` or `ImmutableList` by default.** A composable skips when it gets the same instance again.
- **Map the source before `combine`**, so an object changes only when its source does: checkout does `observeCart().map { it.toOrder() }`.
- **Models from `:shared:domain` are unstable for Compose** (that module has no Compose compiler), and no Compose annotations go there. Where it matters the screen gets its own UI model with plain values: the cart's `CartUiState.Item`.
- **List callbacks take the item's id** (`onQuantityChange: (productId, quantity) -> Unit`), so every card gets the same lambdas and "+" redraws only its own card.
- **Check with compiler reports and a log, not by eye**, and fix only what they show.

## Styled components and previews

- **Every styled element comes from `:core:designsystem`, `:shared:ui` or the feature's `ui` module** (see [Modules](modules.md#where-new-code-goes)); a screen doesn't style Material components itself.
- **Previews sit next to what they preview**, private, Light and Dark. A screen file previews states that look different as a whole screen; a section's states are previewed in the section's file.
