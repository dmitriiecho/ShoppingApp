package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout

internal sealed interface CheckoutEvent {

    data class OnPaymentMethodChange(val method: CheckoutUiState.PaymentMethod) : CheckoutEvent

    data object OnPlaceOrderClick : CheckoutEvent

    data object OnCloseClick : CheckoutEvent
}
