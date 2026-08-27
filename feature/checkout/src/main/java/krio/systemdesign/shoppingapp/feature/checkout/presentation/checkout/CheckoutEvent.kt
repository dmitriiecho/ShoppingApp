package krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout

sealed interface CheckoutEvent {

    data class OnStreetChange(val value: String) : CheckoutEvent

    data class OnApartmentChange(val value: String) : CheckoutEvent

    data class OnCourierCommentChange(val value: String) : CheckoutEvent

    data class OnPaymentMethodChange(val method: PaymentMethod) : CheckoutEvent

    data object OnPlaceOrderClick : CheckoutEvent
}
