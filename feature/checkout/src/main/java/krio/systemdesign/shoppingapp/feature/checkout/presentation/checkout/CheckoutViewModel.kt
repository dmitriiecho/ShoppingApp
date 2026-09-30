package krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import krio.systemdesign.shoppingapp.core.ui.text.UiText
import krio.systemdesign.shoppingapp.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.feature.checkout.R
import krio.systemdesign.shoppingapp.feature.checkout.domain.usecase.PlaceOrderUseCase
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
class CheckoutViewModel @Inject constructor(
    observeCart: ObserveCartUseCase,
    private val placeOrder: PlaceOrderUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Поля формы лежат в SavedStateHandle, чтобы введённый адрес пережил смерть процесса.
    private val formState = combine(
        savedStateHandle.getStateFlow(KEY_STREET, ""),
        savedStateHandle.getStateFlow(KEY_APARTMENT, ""),
        savedStateHandle.getStateFlow(KEY_COURIER_COMMENT, ""),
        savedStateHandle.getStateFlow(KEY_PAYMENT_METHOD, PaymentMethod.Card),
    ) { street, apartment, courierComment, paymentMethod ->
        FormState(street, apartment, courierComment, paymentMethod)
    }
    private val isSubmitting = MutableStateFlow(false)

    private val _effects = Channel<CheckoutEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<CheckoutUiState> = combine(
        observeCart(),
        formState,
        isSubmitting,
    ) { cart, form, submitting ->
        CheckoutUiState(
            items = cart.items,
            subtotal = cart.subtotal(),
            discount = cart.discount(),
            totalPrice = cart.totalPrice(),
            promoCode = cart.promoCode,
            street = form.street,
            apartment = form.apartment,
            courierComment = form.courierComment,
            paymentMethod = form.paymentMethod,
            isSubmitting = submitting,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CheckoutUiState(),
    )

    fun onEvent(event: CheckoutEvent) {
        when (event) {
            is CheckoutEvent.OnStreetChange -> {
                savedStateHandle[KEY_STREET] = event.value
            }
            is CheckoutEvent.OnApartmentChange -> {
                savedStateHandle[KEY_APARTMENT] = event.value
            }
            is CheckoutEvent.OnCourierCommentChange -> {
                savedStateHandle[KEY_COURIER_COMMENT] = event.value
            }
            is CheckoutEvent.OnPaymentMethodChange -> {
                savedStateHandle[KEY_PAYMENT_METHOD] = event.method
            }
            CheckoutEvent.OnPlaceOrderClick -> submitOrder()
        }
    }

    private fun submitOrder() {
        val state = uiState.value
        if (!state.canSubmit) return
        viewModelScope.launch {
            isSubmitting.value = true
            placeOrder()
                .onSuccess { send(CheckoutEffect.Close) }
                .onFailure {
                    send(
                        CheckoutEffect.ShowSnackBar(
                            UiText.Resource(R.string.checkout_place_order_error),
                        ),
                    )
                    isSubmitting.value = false
                }
        }
    }

    private fun send(effect: CheckoutEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private data class FormState(
        val street: String,
        val apartment: String,
        val courierComment: String,
        val paymentMethod: PaymentMethod,
    )

    private companion object {
        const val KEY_STREET = "street"
        const val KEY_APARTMENT = "apartment"
        const val KEY_COURIER_COMMENT = "courier_comment"
        const val KEY_PAYMENT_METHOD = "payment_method"
    }
}
