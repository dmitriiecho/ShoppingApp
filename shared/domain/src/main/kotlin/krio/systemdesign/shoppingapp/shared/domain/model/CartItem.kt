package krio.systemdesign.shoppingapp.shared.domain.model

data class CartItem(
    val productId: String,
    val name: String,
    val imageUrl: String,
    val price: Long, // In US cents.
    val quantity: Int,
    // Stock when the item was added; may be stale, the actual stock comes from cart validation.
    val availableQuantity: Int,
)
