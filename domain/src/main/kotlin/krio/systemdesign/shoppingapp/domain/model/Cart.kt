package krio.systemdesign.shoppingapp.domain.model

import kotlinx.collections.immutable.ImmutableList

data class Cart(
    val items: ImmutableList<CartItem>,
) {
    fun totalPrice(): Long = items.sumOf { it.price * it.quantity }
}
