package krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import krio.systemdesign.shoppingapp.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.feature.checkout.domain.usecase.PlaceOrderUseCase
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
class CheckoutViewModel @Inject constructor(
    observeCart: ObserveCartUseCase,
    private val placeOrder: PlaceOrderUseCase,
) : ViewModel() {

    private val formState = MutableStateFlow(FormState())
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
            totalPrice = cart.totalPrice(),
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
                formState.update { it.copy(street = event.value) }
            }
            is CheckoutEvent.OnApartmentChange -> {
                formState.update { it.copy(apartment = event.value) }
            }
            is CheckoutEvent.OnCourierCommentChange -> {
                formState.update { it.copy(courierComment = event.value) }
            }
            is CheckoutEvent.OnPaymentMethodChange -> {
                formState.update { it.copy(paymentMethod = event.method) }
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
                .onFailure { error ->
                    send(
                        CheckoutEffect.ShowSnackBar(
                            error.message ?: "Не удалось оформить заказ",
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
        val street: String = "",
        val apartment: String = "",
        val courierComment: String = "",
        val paymentMethod: PaymentMethod = PaymentMethod.Card,
    )
}
