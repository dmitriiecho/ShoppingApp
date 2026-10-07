package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import krio.systemdesign.shoppingapp.core.composeutils.state.savedTextField
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.feature.checkout.impl.R
import krio.systemdesign.shoppingapp.feature.checkout.impl.analytics.OrderPlacedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.checkout.impl.domain.usecase.PlaceOrderUseCase
import krio.systemdesign.shoppingapp.shared.analytics.Analytics
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.usecase.ObserveCartUseCase

@HiltViewModel
internal class CheckoutViewModel @Inject constructor(
    observeCart: ObserveCartUseCase,
    private val placeOrder: PlaceOrderUseCase,
    private val analytics: Analytics,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // The form survives process death: the fields and the payment method are kept in SavedStateHandle.
    private val address = CheckoutUiState.Address(
        street = savedStateHandle.savedTextField(KEY_STREET),
        apartment = savedStateHandle.savedTextField(KEY_APARTMENT),
        courierComment = savedStateHandle.savedTextField(KEY_COURIER_COMMENT),
    )
    private val paymentMethod = savedStateHandle.getStateFlow(KEY_PAYMENT_METHOD, CheckoutUiState.PaymentMethod.Card)
    private val isSubmitting = MutableStateFlow(false)

    val uiState: StateFlow<CheckoutUiState> = combine(
        // Mapped here, not inside combine: a new order only when the cart changes, so a payment or
        // submitting change reuses the same object and Compose skips redrawing the order.
        observeCart().map { it.toOrder() },
        paymentMethod,
        isSubmitting,
    ) { order, payment, submitting ->
        CheckoutUiState(
            order = order,
            address = address,
            paymentMethod = payment,
            isSubmitting = submitting,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CheckoutUiState(address = address),
    )

    private val _effects = Channel<CheckoutEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: CheckoutEvent) {
        when (event) {
            is CheckoutEvent.OnPaymentMethodChange -> changePaymentMethod(event.method)
            CheckoutEvent.OnPlaceOrderClick -> submitOrder()
            CheckoutEvent.OnCloseClick -> send(CheckoutEffect.Close)
        }
    }

    private fun changePaymentMethod(method: CheckoutUiState.PaymentMethod) {
        savedStateHandle[KEY_PAYMENT_METHOD] = method
    }

    // Places the order at most once: the check reads isSubmitting itself, since uiState gets it only after
    // combine and a fast second tap would slip through; on success the flag stays true while the screen closes.
    private fun submitOrder() {
        if (isSubmitting.value || !uiState.value.canSubmit) return
        // Taken before placing: the order resets the cart.
        val order = uiState.value.order as? CheckoutUiState.Order.Loaded ?: return
        isSubmitting.value = true
        viewModelScope.launch {
            placeOrder()
                .onSuccess {
                    analytics.log(order.toAnalyticsEvent())
                    send(CheckoutEffect.Close)
                }
                .onFailure {
                    send(CheckoutEffect.ShowSnackBar(UiText.Resource(R.string.checkout_place_order_error)))
                    isSubmitting.value = false
                }
        }
    }

    private fun send(effect: CheckoutEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private companion object {
        const val KEY_STREET = "street"
        const val KEY_APARTMENT = "apartment"
        const val KEY_COURIER_COMMENT = "courier_comment"
        const val KEY_PAYMENT_METHOD = "payment_method"
    }
}

private fun CheckoutUiState.Order.Loaded.toAnalyticsEvent() = OrderPlacedAnalyticsEvent(
    itemCount = items.sumOf { it.quantity },
    totalCents = total,
    promoCode = promoCode?.code,
)

private fun Cart.toOrder() = CheckoutUiState.Order.Loaded(
    items = items,
    subtotal = subtotal(),
    discount = discount(),
    total = totalPrice(),
    promoCode = promoCode,
)
