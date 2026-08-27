package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import krio.systemdesign.shoppingapp.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.RemoveFromCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.UpdateCartQuantityUseCase
import krio.systemdesign.shoppingapp.feature.cart.domain.usecase.AcceptCartChangesUseCase
import krio.systemdesign.shoppingapp.feature.cart.domain.usecase.ValidateCartUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    observeCart: ObserveCartUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    private val validateCart: ValidateCartUseCase,
    private val acceptCartChanges: AcceptCartChangesUseCase,
) : ViewModel() {

    private val promoState = MutableStateFlow(PromoState())
    private val issuesState = MutableStateFlow<List<ItemIssue>>(emptyList())
    private val isValidating = MutableStateFlow(false)

    private val _effects = Channel<CartEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<CartUiState> = combine(
        observeCart(),
        promoState,
        issuesState,
        isValidating,
    ) { cart, promo, issues, validating ->
        CartUiState(
            items = cart.items,
            totalPrice = cart.totalPrice(),
            appliedPromoCode = promo.code,
            showPromoSuccess = promo.showSuccess,
            issues = issues.toPersistentList(),
            isValidating = validating,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartUiState(),
    )

    fun onPromoApplied(promoCode: String) {
        promoState.value = PromoState(code = promoCode, showSuccess = true)
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
            CartEvent.OnAcceptChanges -> acceptPendingChanges()
            CartEvent.OnDismissIssues -> issuesState.value = emptyList()
            CartEvent.OnPromoSuccessShown -> {
                promoState.update { it.copy(showSuccess = false) }
            }
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

    private data class PromoState(
        val code: String? = null,
        val showSuccess: Boolean = false,
    )
}
