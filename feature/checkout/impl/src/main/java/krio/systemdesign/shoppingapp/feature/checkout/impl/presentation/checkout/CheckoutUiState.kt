package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout

import androidx.compose.foundation.text.input.TextFieldState
import krio.systemdesign.shoppingapp.shared.domain.model.CartItem
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

internal data class CheckoutUiState(
    val order: Order = Order.Loading,
    val address: Address,
    val paymentMethod: PaymentMethod = PaymentMethod.Card,
    val isSubmitting: Boolean = false,
) {
    private val hasItems: Boolean
        get() = order is Order.Loaded && order.items.isNotEmpty()

    // The order stays on screen while it's being placed, though the cart is already empty by then.
    val showsOrder: Boolean
        get() = hasItems || isSubmitting

    val canSubmit: Boolean
        get() = hasItems && address.street.text.isNotBlank() && !isSubmitting

    // The order from the cart: Loading until Room returns it.
    sealed interface Order {
        data object Loading : Order

        data class Loaded(
            val items: List<CartItem>,
            val subtotal: Long,
            val discount: Long,
            val total: Long,
            val promoCode: PromoCode?,
        ) : Order
    }

    // State holders, not Strings: the fields edit them in place, so typing can't lose characters.
    // They're the same instances for the whole screen.
    data class Address(
        val street: TextFieldState,
        val apartment: TextFieldState,
        val courierComment: TextFieldState,
    )

    enum class PaymentMethod {
        Card,
        Cash,
    }
}
