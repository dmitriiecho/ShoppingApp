package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import krio.systemdesign.shoppingapp.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.PromoCode
import krio.systemdesign.shoppingapp.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.RemoveFromCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.UpdateCartQuantityUseCase
import krio.systemdesign.shoppingapp.feature.cart.domain.usecase.AcceptCartChangesUseCase
import krio.systemdesign.shoppingapp.feature.cart.domain.usecase.ApplyPromoCodeUseCase
import krio.systemdesign.shoppingapp.feature.cart.domain.usecase.RemovePromoCodeUseCase
import krio.systemdesign.shoppingapp.feature.cart.domain.usecase.ValidateCartUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    observeCart: ObserveCartUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    private val validateCart: ValidateCartUseCase,
    private val acceptCartChanges: AcceptCartChangesUseCase,
    private val applyPromoCode: ApplyPromoCodeUseCase,
    private val removePromoCode: RemovePromoCodeUseCase,
) : ViewModel() {

    private val issuesState = MutableStateFlow<List<ItemIssue>>(emptyList())
    private val isValidating = MutableStateFlow(false)

    private val _effects = Channel<CartEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<CartUiState> = combine(
        observeCart(),
        issuesState,
        isValidating,
    ) { cart, issues, validating ->
        CartUiState(
            items = cart.items,
            subtotal = cart.subtotal(),
            discount = cart.discount(),
            totalPrice = cart.totalPrice(),
            promoCode = cart.promoCode,
            issues = issues.toPersistentList(),
            isValidating = validating,
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
                    send(CartEffect.ShowSnackBar("Промокод «${promoCode.code}» применён"))
                }
                .onFailure { error ->
                    send(CartEffect.ShowSnackBar(error.message ?: "Не удалось применить промокод"))
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
            CartEvent.OnCheckoutClick -> checkout()
            CartEvent.OnPromoClick -> send(CartEffect.NavigateToPromo)
            CartEvent.OnRemovePromoClick -> launchCartAction { removePromoCode() }
            CartEvent.OnAcceptChanges -> acceptPendingChanges()
            CartEvent.OnDismissIssues -> issuesState.value = emptyList()
        }
    }

    private fun checkout() {
        if (uiState.value.isEmpty) {
            send(CartEffect.ShowSnackBar("Корзина пуста"))
            return
        }
        viewModelScope.launch {
            isValidating.value = true
            when (val result = validateCart()) {
                CartValidationResult.Success -> send(CartEffect.NavigateToCheckout)
                is CartValidationResult.Invalid -> issuesState.value = result.issues
                is CartValidationResult.Error -> {
                    send(
                        CartEffect.ShowSnackBar(
                            result.error.message ?: "Не удалось проверить корзину",
                        ),
                    )
                }
            }
            isValidating.value = false
        }
    }

    private fun acceptPendingChanges() {
        viewModelScope.launch {
            val issues = issuesState.value
            acceptCartChanges(issues)
                .onSuccess { issuesState.value = emptyList() }
                .onFailure { error ->
                    send(CartEffect.ShowSnackBar(error.message ?: "Не удалось обновить корзину"))
                }
        }
    }

    private fun launchCartAction(action: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            action().onFailure { error ->
                send(CartEffect.ShowSnackBar(error.message ?: "Не удалось обновить корзину"))
            }
        }
    }

    private fun send(effect: CartEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
