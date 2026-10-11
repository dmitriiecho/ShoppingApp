package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.ViewModelKey
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.ViewModelScope
import krio.systemdesign.shoppingapp.feature.cart.impl.R
import krio.systemdesign.shoppingapp.feature.cart.impl.analytics.CartClearedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.cart.impl.analytics.CheckoutStartedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.cart.impl.analytics.PromoCodeAppliedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.model.CartValidation
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.model.toCartValidation
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase.AcceptCartChangesUseCase
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase.ApplyPromoCodeUseCase
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase.ClearCartItemsUseCase
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase.RemovePromoCodeUseCase
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase.ValidateCartUseCase
import krio.systemdesign.shoppingapp.shared.analytics.Analytics
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen
import krio.systemdesign.shoppingapp.shared.analytics.event.cart.CartQuantityChangedAnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.event.cart.RemoveFromCartAnalyticsEvent
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode
import krio.systemdesign.shoppingapp.shared.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.RemoveFromCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.UpdateCartQuantityUseCase

@ViewModelKey
@ContributesIntoMap(ViewModelScope::class)
internal class CartViewModel(
    private val observeCart: ObserveCartUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    private val validateCart: ValidateCartUseCase,
    private val acceptCartChanges: AcceptCartChangesUseCase,
    private val applyPromoCode: ApplyPromoCodeUseCase,
    private val removePromoCode: RemovePromoCodeUseCase,
    private val clearCartItems: ClearCartItemsUseCase,
    private val analytics: Analytics,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val lastValidation = MutableStateFlow(CartValidation())
    private val isOpeningCheckout = MutableStateFlow(false)
    private val isClearCartDialogVisible = savedStateHandle.getStateFlow(KEY_CLEAR_CART_DIALOG_VISIBLE, false)

    // The validation in progress. Tapping Checkout while the one started when the screen showed is running waits for it
    // instead of starting a second one.
    private var runningValidation: Deferred<ValidationOutcome>? = null

    val uiState: StateFlow<CartUiState> = combine(
        // Built here, not in the outer combine: new content only when the cart or its last validation changes,
        // so opening the dialog or starting checkout reuses the same items and Compose skips redrawing them.
        combine(observeCart(), lastValidation) { cart, validation -> cart.toContent(validation) },
        isOpeningCheckout,
        isClearCartDialogVisible,
    ) { content, openingCheckout, clearCartDialogVisible ->
        CartUiState(
            content = content,
            isOpeningCheckout = openingCheckout,
            isClearCartDialogVisible = clearCartDialogVisible,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartUiState(),
    )

    private val _effects = Channel<CartEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.OnItemClick -> openProduct(event.productId, event.productName, event.imageUrl)
            is CartEvent.OnQuantityChange -> changeQuantity(event.productId, event.quantity)
            is CartEvent.OnRemoveFromCartClick -> removeItem(event.productId)
            CartEvent.OnCheckoutClick -> openCheckout()
            CartEvent.OnScreenShown -> startValidation()
            CartEvent.OnPromoCodeClick -> send(CartEffect.NavigateToPromo)
            CartEvent.OnRemovePromoCodeClick -> removePromo()
            is CartEvent.OnPromoCodeApplied -> applyPromo(event.promoCode)
            CartEvent.OnClearCartClick -> setClearCartDialogVisible(true)
            CartEvent.OnClearCartConfirmClick -> clearCart()
            CartEvent.OnClearCartDialogDismiss -> setClearCartDialogVisible(false)
            CartEvent.OnAcceptNewPricesClick -> acceptNewPrices()
            CartEvent.OnRemoveUnavailableClick -> removeUnavailableItems()
            CartEvent.OnBackClick -> send(CartEffect.NavigateBack)
        }
    }

    private fun openProduct(
        productId: String,
        productName: String,
        imageUrl: String,
    ) {
        send(CartEffect.NavigateToProduct(productId, productName, imageUrl))
    }

    private fun changeQuantity(
        productId: String,
        quantity: Int,
    ) {
        launchCartAction(
            action = { updateCartQuantity(productId, quantity) },
            onSuccess = {
                analytics.log(CartQuantityChangedAnalyticsEvent(productId, quantity, AnalyticsScreen.Cart))
            },
        )
    }

    private fun removeItem(productId: String) {
        launchCartAction(
            action = { removeFromCart(productId) },
            onSuccess = { analytics.log(RemoveFromCartAnalyticsEvent(productId, AnalyticsScreen.Cart)) },
        )
    }

    private fun applyPromo(promoCode: PromoCode) {
        viewModelScope.launch {
            applyPromoCode(promoCode)
                .onSuccess {
                    analytics.log(PromoCodeAppliedAnalyticsEvent(promoCode.code, promoCode.discountPercent))
                    showSnackBar(R.string.cart_promo_applied, promoCode.code)
                }
                .onFailure { showSnackBar(R.string.cart_promo_apply_error) }
        }
    }

    private fun removePromo() {
        launchCartAction { removePromoCode() }
    }

    private fun setClearCartDialogVisible(isVisible: Boolean) {
        savedStateHandle[KEY_CLEAR_CART_DIALOG_VISIBLE] = isVisible
    }

    private fun clearCart() {
        setClearCartDialogVisible(false)
        launchCartAction(
            action = { clearCartItems() },
            onSuccess = { analytics.log(CartClearedAnalyticsEvent()) },
        )
    }

    private fun acceptNewPrices() {
        launchCartAction { acceptCartChanges(pendingIssues().filterIsInstance<ItemIssue.PriceChanged>()) }
    }

    private fun removeUnavailableItems() {
        launchCartAction { acceptCartChanges(pendingIssues().filterIsInstance<ItemIssue.Unavailable>()) }
    }

    // Read from the sources, not uiState: it gets them only after combine and may lag behind.
    private suspend fun pendingIssues(): List<ItemIssue> = lastValidation.value.pendingIssues(observeCart().first())

    // Starts a validation unless one is already running; its result reaches the screen through the notices.
    private fun startValidation() {
        if (runningValidation?.isActive == true) return
        runningValidation = viewModelScope.async { runValidation() }
    }

    // Validates the cart on the server and updates the change notices, with no loading or messages.
    private suspend fun runValidation(): ValidationOutcome {
        val cart = observeCart().first()
        if (cart.items.isEmpty()) return ValidationOutcome.EmptyCart
        val result = validateCart()
        result.toCartValidation(cart)?.let { lastValidation.value = it }
        return ValidationOutcome.Validated(cart, result)
    }

    // The cart stays editable while Checkout waits, so it can change during a request: then validate again.
    // Each extra round needs another edit by the user, so the loop can't spin by itself.
    private suspend fun validateCurrentCart(): ValidationOutcome {
        while (true) {
            startValidation()
            val outcome = checkNotNull(runningValidation).await()
            if (outcome !is ValidationOutcome.Validated || outcome.cart == observeCart().first()) return outcome
        }
    }

    // Opens checkout if the cart passes validation; otherwise says what's wrong.
    private fun openCheckout() {
        if (isOpeningCheckout.value) return
        isOpeningCheckout.value = true
        viewModelScope.launch {
            try {
                when (val outcome = validateCurrentCart()) {
                    // The cart was emptied during the validation; the screen already shows the empty state.
                    ValidationOutcome.EmptyCart -> Unit
                    is ValidationOutcome.Validated -> finishOpeningCheckout(outcome)
                }
            } finally {
                isOpeningCheckout.value = false
            }
        }
    }

    private fun finishOpeningCheckout(outcome: ValidationOutcome.Validated) {
        when (outcome.result) {
            CartValidationResult.Success -> {
                val cart = outcome.cart
                analytics.log(CheckoutStartedAnalyticsEvent(cart.items.sumOf { it.quantity }, cart.totalPrice()))
                send(CartEffect.NavigateToCheckout)
            }
            is CartValidationResult.Invalid -> showSnackBar(R.string.cart_changed)
            is CartValidationResult.Error -> showSnackBar(R.string.cart_validation_error)
        }
    }

    private fun launchCartAction(
        onSuccess: () -> Unit = {},
        action: suspend () -> Result<Unit>,
    ) {
        viewModelScope.launch {
            action()
                .onSuccess { onSuccess() }
                .onFailure { showSnackBar(R.string.cart_update_error) }
        }
    }

    private fun showSnackBar(
        @StringRes message: Int,
        vararg args: Any,
    ) {
        send(CartEffect.ShowSnackBar(UiText.Resource(message, args.toList())))
    }

    private fun send(effect: CartEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private companion object {
        const val KEY_CLEAR_CART_DIALOG_VISIBLE = "clear_cart_dialog_visible"
    }
}

private sealed interface ValidationOutcome {
    // The cart was empty: nothing to validate.
    data object EmptyCart : ValidationOutcome

    // The server's answer together with the cart it validated. Kept raw, not as the CartValidation behind the
    // notices, so that whether checkout opens is the server's call, not our reading of its issues.
    data class Validated(
        val cart: Cart,
        val result: CartValidationResult,
    ) : ValidationOutcome
}
