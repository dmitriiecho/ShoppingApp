package krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout

import krio.systemdesign.shoppingapp.domain.model.CartItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class PaymentMethod {
    Card,
    Cash,
}

data class CheckoutUiState(
    val items: ImmutableList<CartItem> = persistentListOf(),
    val totalPrice: Long = 0,
    val street: String = "",
    val apartment: String = "",
    val courierComment: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.Card,
    val isSubmitting: Boolean = false,
) {
    val isEmpty: Boolean get() = items.isEmpty()

    val canSubmit: Boolean
        get() = !isEmpty && street.isNotBlank() && !isSubmitting
}
