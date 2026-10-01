package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import krio.systemdesign.shoppingapp.core.ui.text.UiText
import krio.systemdesign.shoppingapp.domain.model.CartItem
import krio.systemdesign.shoppingapp.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.PromoCode
import krio.systemdesign.shoppingapp.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.RemoveFromCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.UpdateCartQuantityUseCase
import krio.systemdesign.shoppingapp.feature.cart.R
import krio.systemdesign.shoppingapp.feature.cart.domain.usecase.AcceptCartChangesUseCase
import krio.systemdesign.shoppingapp.feature.cart.domain.usecase.ApplyPromoCodeUseCase
import krio.systemdesign.shoppingapp.feature.cart.domain.usecase.ClearCartItemsUseCase
import krio.systemdesign.shoppingapp.feature.cart.domain.usecase.RemovePromoCodeUseCase
import krio.systemdesign.shoppingapp.feature.cart.domain.usecase.ValidateCartUseCase
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val observeCart: ObserveCartUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    private val validateCart: ValidateCartUseCase,
    private val acceptCartChanges: AcceptCartChangesUseCase,
    private val applyPromoCode: ApplyPromoCodeUseCase,
    private val removePromoCode: RemovePromoCodeUseCase,
    private val clearCartItems: ClearCartItemsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val foundChanges = MutableStateFlow(FoundChanges())
    private val isValidating = MutableStateFlow(false)
    private val isClearCartDialogVisible =
        savedStateHandle.getStateFlow(KEY_CLEAR_CART_DIALOG_VISIBLE, false)

    private val _effects = Channel<CartEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<CartUiState> = combine(
        observeCart(),
        foundChanges,
        isValidating,
        isClearCartDialogVisible,
    ) { cart, changes, validating, clearCartDialogVisible ->
        val isPromoCodeValid = changes.invalidPromoCode == null ||
            cart.promoCode?.code != changes.invalidPromoCode
        CartUiState(
            items = cart.items,
            subtotal = cart.subtotal(),
            // Недействующий промокод скидки не даёт: в сумме его не учитываем, хотя он ещё не убран.
            discount = if (isPromoCodeValid) cart.discount() else 0,
            totalPrice = if (isPromoCodeValid) cart.totalPrice() else cart.subtotal(),
            promoCode = cart.promoCode,
            // Пометки вычисляются из корзины, поэтому исправленное любым способом исчезает само.
            itemIssues = changes.issues
                .filter { it.isPending(cart.items) }
                .associateBy { it.itemId }
                .toImmutableMap(),
            isPromoCodeValid = isPromoCodeValid,
            isValidating = validating,
            isClearCartDialogVisible = clearCartDialogVisible,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartUiState(),
    )

    fun onPromoApplied(promoCode: PromoCode) {
        viewModelScope.launch {
            applyPromoCode(promoCode)
                .onSuccess {
                    val message = UiText.Resource(R.string.cart_promo_applied, listOf(promoCode.code))
                    send(CartEffect.ShowSnackBar(message))
                }
                .onFailure {
                    send(CartEffect.ShowSnackBar(UiText.Resource(R.string.cart_promo_apply_error)))
                }
        }
    }

    fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.OnItemClick -> {
                send(CartEffect.NavigateToProduct(event.productId, event.productName))
            }
            is CartEvent.OnUpdateQuantity -> {
                launchCartAction {
                    updateCartQuantity(event.productId, event.quantity)
                }
            }
            is CartEvent.OnRemoveItem -> {
                launchCartAction {
                    removeFromCart(event.productId)
                }
            }
            CartEvent.OnCheckoutClick -> validate(isCheckout = true)
            CartEvent.OnScreenShown -> validate(isCheckout = false)
            CartEvent.OnPromoClick -> send(CartEffect.NavigateToPromo)
            CartEvent.OnRemovePromoClick -> launchCartAction { removePromoCode() }
            CartEvent.OnClearCartClick -> {
                savedStateHandle[KEY_CLEAR_CART_DIALOG_VISIBLE] = true
            }
            CartEvent.OnClearCartConfirmed -> {
                savedStateHandle[KEY_CLEAR_CART_DIALOG_VISIBLE] = false
                launchCartAction { clearCartItems() }
            }
            CartEvent.OnClearCartDismiss -> {
                savedStateHandle[KEY_CLEAR_CART_DIALOG_VISIBLE] = false
            }
            CartEvent.OnAcceptNewPricesClick -> launchCartAction {
                acceptCartChanges(uiState.value.itemIssues.values.filterIsInstance<ItemIssue.PriceChanged>())
            }
            CartEvent.OnRemoveUnavailableClick -> launchCartAction {
                acceptCartChanges(uiState.value.itemIssues.values.filterIsInstance<ItemIssue.Unavailable>())
            }
        }
    }

    // Проверяет корзину на сервере. При показе экрана — молча: только обновляет пометки.
    // При оформлении — сообщает о результате, а если всё в порядке, открывает оформление.
    private fun validate(isCheckout: Boolean) {
        if (isValidating.value) return
        isValidating.value = true
        viewModelScope.launch {
            try {
                val cart = observeCart().first()
                if (cart.items.isEmpty()) {
                    if (isCheckout) send(CartEffect.ShowSnackBar(UiText.Resource(R.string.cart_empty_title)))
                    return@launch
                }
                when (val result = validateCart()) {
                    CartValidationResult.Success -> {
                        foundChanges.value = FoundChanges()
                        if (isCheckout) send(CartEffect.NavigateToCheckout)
                    }
                    is CartValidationResult.Invalid -> {
                        foundChanges.value = FoundChanges(
                            issues = result.issues,
                            // Запоминаем сам код: если потом применят другой, пометка к нему не относится.
                            invalidPromoCode = cart.promoCode?.code.takeUnless { result.isPromoCodeValid },
                        )
                        if (isCheckout) send(CartEffect.ShowSnackBar(UiText.Resource(R.string.cart_changed)))
                    }
                    is CartValidationResult.Error -> {
                        if (isCheckout) send(CartEffect.ShowSnackBar(UiText.Resource(R.string.cart_validation_error)))
                    }
                }
            } finally {
                isValidating.value = false
            }
        }
    }

    private fun launchCartAction(action: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            action().onFailure {
                send(CartEffect.ShowSnackBar(UiText.Resource(R.string.cart_update_error)))
            }
        }
    }

    private fun send(effect: CartEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private companion object {
        const val KEY_CLEAR_CART_DIALOG_VISIBLE = "clear_cart_dialog_visible"
    }
}

// Что нашла последняя проверка корзины.
private data class FoundChanges(
    val issues: List<ItemIssue> = emptyList(),
    // Код промокода, который проверка признала недействующим.
    val invalidPromoCode: String? = null,
)

// Изменение ещё не исправлено: товар по-прежнему в корзине, а для новой цены — цена в корзине ещё старая.
private fun ItemIssue.isPending(items: List<CartItem>): Boolean = when (this) {
    is ItemIssue.Unavailable -> items.any { it.productId == productId }
    is ItemIssue.PriceChanged -> items.any { it.productId == productId && it.price != newPrice }
}

private val ItemIssue.itemId: String
    get() = when (this) {
        is ItemIssue.Unavailable -> productId
        is ItemIssue.PriceChanged -> productId
    }
