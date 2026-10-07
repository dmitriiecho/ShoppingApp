package krio.systemdesign.shoppingapp.shared.domain.model

import kotlinx.collections.immutable.ImmutableList

data class Cart(
    val items: ImmutableList<CartItem>,
    val promoCode: PromoCode? = null,
) {
    fun subtotal(): Long = items.sumOf { it.price * it.quantity }

    fun discount(): Long = promoCode?.discountFor(subtotal()) ?: 0

    fun totalPrice(): Long = subtotal() - discount()

    // 0 when the product isn't in the cart.
    fun quantityOf(productId: String): Int = items.firstOrNull { it.productId == productId }?.quantity ?: 0
}
